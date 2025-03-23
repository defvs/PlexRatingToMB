import com.xenomachina.argparser.ArgParser
import me.tongfei.progressbar.ProgressBar
import musicbrainz.MusicBrainzClient
import org.http4k.core.Status
import plex.PlexClient
import plex.api.LibraryTypes
import plex.api.TrackResponse
import subsonic.SubsonicClient
import java.io.File
import kotlin.math.ceil
import kotlin.math.roundToInt

data class Track(
    val path: String,
    val trackMbid: String?,
    val userRating: Float?,
) {
    constructor(track: TrackResponse.Track) : this(track.path!!, track.trackMbid, track.userRating?.toFloat())
}

fun main(args: Array<String>) {
    val args1 = ArgParser(args).parseInto(::Args)
    val options = Options(
        baseUrl = args1.baseUrl ?: askUser("Plex server URL: "),
        plexToken = args1.plexToken ?: askUser("Your X-Plex-Token: "),
        outputFile = args1.outputFile,
        mbUsername = args1.mbUsername,
        mbPassword = args1.mbPassword,
        libraryId = args1.libraryId,
        subsonicUrl = args1.subsonicUrl,
        subsonicToken = args1.subsonicToken,
    )

    runPlex(options)
        .also { runMusicBrainz(options, it) }
        .also { runSubsonic(options, it) }
}

fun runPlex(options: Options): List<Track> {
    val client = PlexClient(options.baseUrl, options.plexToken)

    val musicLibraries = client.getLibraries(LibraryTypes.Music)
    options.libraryId = askUser(
        """
Please enter the ID of the library to export.
${
            musicLibraries.sortedBy { it.key.toInt() }.joinToString(separator = "\n") { library ->
                "${library.key}\t${library.locations.joinToString(separator = ", ") { it.path }}"
            }
        }

ID: 
        """.trimIndent()
    )

    val libraryToBeFetched = musicLibraries.singleOrNull { it.key == options.libraryId }?.key
        ?: throw Exception("No library found for id ${options.libraryId}")
    println()


    val trackRatingKeys = run {
        var progressBar: ProgressBar? = null
        client.getLibraryTracks(
            libraryToBeFetched,
            { totalSize -> progressBar = ProgressBar("Get tracks in library", totalSize.toLong()) },
            { processed -> progressBar!!.stepTo(processed.toLong()) }
        ).mapNotNull { it.ratingKey }.also {
            progressBar!!.close()
            println()
        }

    }

    val tracks = run {
        val progressBar = ProgressBar("Get track details", trackRatingKeys.size.toLong())
        trackRatingKeys.map {
            progressBar.step()
            Track(client.getTrack(it))
        }
    }

    println(
        """
        
        Processed ${tracks.count()} total tracks:
        - ${tracks.count { it.trackMbid != null }} with MBIDs
        - ${tracks.count { it.userRating != null }} with ratings
        - ${tracks.count { it.trackMbid != null && it.userRating != null }} with both
        """.trimIndent()
    )

    if (options.outputFile != null) {
        File(options.outputFile).writeText(tracks.joinToString(separator = "\n") { """"${it.path}","${it.trackMbid}","${it.userRating}"""" })
        println("Exported details to ${options.outputFile}.")
    }

    return tracks
}

fun runMusicBrainz(options: Options, tracks: List<Track>) {
    if (options.mbUsername.isNullOrBlank() || options.mbPassword.isNullOrBlank())
        println("No MusicBrainz credentials passed, skipping (pass with --mbusername and --mbpassword).").also { return }

    val client = MusicBrainzClient(options.mbUsername!!, options.mbPassword!!)
    val total: Int

    tracks.filter { it.trackMbid != null && it.userRating != null }
        .also { total = it.size }
        .associate { it.trackMbid!! to it.userRating!!.times(10).roundToInt() }.run {
            val progressBar = ProgressBar("Convert Track MBID to Recording MBID", size.toLong())
            mapKeys {
                progressBar.step()
                Thread.sleep(1000)
                lateinit var lastException: Exception
                for (i in 1..10) try { // Rate limiting
                    return@mapKeys client.trackToRecordingMBID(it.key)
                } catch (e: Exception) {
                    println("Try $i/10...")
                    lastException = e
                    Thread.sleep(1000)
                }
                throw Exception("Failed to convert Track MBID to Recording MBID after 10 tries.", lastException)
            }.also {
                progressBar.close()
                println()
            }
        }
        .let { client.submitRatings(it) }
        .also {
            when (it) {
                Status.OK -> println("Successfully submitted $total track ratings.")
                else -> println("An error has occured while submitting track ratings: $it")
            }
        }
}

fun runSubsonic(options: Options, tracks: List<Track>) {
    if (options.subsonicUrl.isNullOrBlank() || options.subsonicToken.isNullOrBlank())
        println("No Subsonic URL or key passed, skipping (pass with --subsonicurl and --subsonictoken).").also { return }

    val client = SubsonicClient(options.subsonicUrl!!, options.subsonicToken!!)
    val ratings = client.getAllTracks().filter { it.musicBrainzId != null }.mapNotNull { subsonicTracks ->
        tracks.firstOrNull { track -> track.userRating != null && track.trackMbid == subsonicTracks.musicBrainzId }?.let {
            val rating = ceil(it.userRating!!.div(2)).roundToInt().coerceIn(0..5)
            subsonicTracks.id to rating
        }
    }.toMap()

    println("Total matching tracks: ${ratings.size}")

    client.submitRatings(ratings)
}

fun askUser(question: String): String {
    print(question)
    return readlnOrNull() ?: ""
}

