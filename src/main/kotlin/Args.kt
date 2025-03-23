import com.xenomachina.argparser.ArgParser
import util.optional

class Args(parser: ArgParser) {
    val baseUrl by parser.storing(
        "-u", "--url",
        help = "Base URL of your Plex server"
    ) { removeSuffix("/") }.optional()

    val plexToken by parser.storing(
        "-t", "--token",
        help = "Your X-Plex-Token"
    ).optional()

    val libraryId by parser.storing(
        "-l", "--library",
        help = "Library ID (sometimes referred to as Section ID) of the library to export"
    ).optional()

    val outputFile by parser.storing(
        "-o", "--output",
        help = "CSV Output file with ratings"
    ).optional()

    val mbUsername by parser.storing(
        "--mbusername",
        help = "MusicBrainz Username"
    ).optional()

    val mbPassword by parser.storing(
        "--mbpassword",
        help = "MusicBrainz Password"
    ).optional()

    val subsonicUrl by parser.storing(
        "--subsonicurl",
        help = "Subsonic-compatible server URL"
    ).optional()

    val subsonicToken by parser.storing(
        "--subsonictoken",
        help = "Subsonic API Token"
    ).optional()
}

class Options(
    val baseUrl: String,
    val plexToken: String,
    val outputFile: String?,
    val mbUsername: String?,
    val mbPassword: String?,
    var libraryId: String? = null,
    var subsonicUrl: String? = null,
    var subsonicToken: String? = null,
)
