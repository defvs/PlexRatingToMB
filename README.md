(R=W, V=3)

# Plex Ratings Exporter

Plex Ratings Exporter is a tool designed to export ratings from your Plex server library into a CSV file and/or directly to MusicBrainz.

## Usage

The software can operate either in Interactive Mode (aka - the software asks for each option) or Batch Mode.
Any argument sent through the command line will not be asked, except for some (see parenthesis)

You will need to provide various command-line arguments. Below are the details for each argument:

### Command-Line Arguments

- `-u`, `--url` (optional, _asked for in Interactive Mode_):
    - **Description**: Base URL of your Plex server.
    - **Example**: `http://localhost:32400`

- `-t`, `--token` (optional, _asked for in Interactive Mode_):
    - **Description**: Your X-Plex-Token. This can be obtained from Dev Tools > Network by inspecting the query parameters of any call to the server on your Plex webpage.
    - **Example**: `ABC123XYZ`

- `-l`, `--library` (optional, _asked for in Interactive Mode_):
    - **Description**: Library ID (sometimes referred to as Section ID) of the library to export.
    - **Example**: `1`

- `-o`, `--output` (optional, **not asked in Interactive Mode**):
    - **Description**: CSV Output file with ratings. If not provided, this step will be skipped.
    - **Example**: `ratings.csv`

- `--mbusername` (optional, **not asked in Interactive Mode**):
    - **Description**: MusicBrainz Username. If not provided, the export to MusicBrainz will be skipped.
    - **Example**: `your_mb_username`

- `--mbpassword` (optional, **not asked in Interactive Mode**):
    - **Description**: MusicBrainz Password. If not provided, the export to MusicBrainz will be skipped.
    - **Example**: `your_mb_password`

### Example Command (Interactive)

```sh
java -jar PlexRatingToMB.jar \
  --output ratings.csv \
  --mbusername your_mb_username \
  --mbpassword your_mb_password
```

### Example Command (Batch)

```sh
java -jar PlexRatingToMB.jar \
  --url http://localhost:32400 \
  --token ABC123XYZ \
  --library 1 \
  --output ratings.csv \
  --mbusername your_mb_username \
  --mbpassword your_mb_password
```

### Argument Details

- **Base URL (`-u`, `--url`)**: This is the address of your Plex server. If not provided, the tool will attempt to use a default URL. Ensure the URL does not have a trailing slash.

- **Plex Token (`-t`, `--token`)**: The X-Plex-Token is required for authentication with your Plex server. It can be found in the query parameters of any call to the server on your Plex webpage using Dev Tools > Network.

- **Library ID (`-l`, `--library`)**: This ID corresponds to the specific library from which you want to export ratings. Each library in Plex has a unique section ID. This parameter is optional, and if omitted, the tool may prompt for the library ID.

- **Output File (`-o`, `--output`)**: The name of the CSV file where the exported ratings will be saved. If not provided, this step will be skipped.

- **MusicBrainz Username (`--mbusername`)**: Your username for MusicBrainz, a music database used for retrieving additional metadata. If not provided, the export to MusicBrainz will be skipped.

- **MusicBrainz Password (`--mbpassword`)**: Your password for MusicBrainz. This is required along with your username to access the MusicBrainz API. If not provided, the export to MusicBrainz will be skipped.

## License

This project is licensed under GPLv3. See the [LICENSE](LICENSE) file for details.
