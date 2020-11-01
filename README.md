# Composer [![Codacy Badge](https://api.codacy.com/project/badge/Grade/e14287e74fb44aeeb1c294ff7959076e)](https://www.codacy.com/app/sarhatabaot/Composer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sarhatabaot/Composer&amp;utm_campaign=Badge_Grade) [![Build Status](https://travis-ci.org/sarhatabaot/Composer.svg?branch=api-7)](https://travis-ci.org/sarhatabaot/Composer)

#### This version has been updated to work with Sponge API v7.
Composer is a full featured note block music importer and player. Import Note Block Studio 151 files by placing them in the config/composer/tracks/ directory and the plugin will automatically load them on start.

Composer comes with a full music player built in as shown below. There are commands for each action you can perform within this menu as well as the text itself being clickable.

![Composer Example](./images/composer-overview.png)

## Features
* Per player music player for loaded tracks.
* Play, pause, shuffle, skip, back actions.
* Flexible API to allow developers to create compositions programmatically or import other file formats.

## Installation
1. Place the composer-x.x-SNAPSHOT.jar file in your mods/ folder.
2. Place your .nbs files in the config/composer/tracks/ directory.
3. Run the server.


## Playlists
As of v4.1.0 Composer support playlists. To use playlists enable the option in default.conf
```
use-playlists=true
default-playlist="default"
```
### Creating a playlist
1. Under config/composer/playlists create a folder with the desired name. 
2. Place your .nbs files in the folder you created.
3. Run the server.

### Set a playlist 
Run the `/music playlist <playlist>` command.

### Setting a default playlist
Change the config option to the playlist name.
```
default-playlist="default"
```
## Commands
Note: The -p lets you target a player other than yourself.
* `/composer` or `/music`: Base command for plugin. These can be used interchangeably.
* `/music list`
        Aliases: `list-tracks`, `tracks`, `track-list`
* `/music play [-p <player>] <trackNumber>`
        Aliases: `start`, `>`
* `/music play-once [-p <player>] <trackNumber>`
* `/music pause [-p <player>]`
        Aliases: `||`
* `/music stop`
* `/music resume [-p <player>]`
* `/music shuffle [-p <player>]`
* `/music queue [-p <player>]`: Shows this player’s play queue
        Aliases: `order`
* `/music next [-p <player>]`
        Aliases: `skip`, `>|`
* `/music previous [-p <player>]`
        Aliases: `back`, `|<`
* `/music playlist <playlist> [-p <player>]`: Sets an active playlist.
* `/music list-playlist`
        Aliases: `playlists`
* `/music loop-track`: Loops the track.
* `/music loop-playlist`: Loops the playlist.
        

        
## Permissions
* `composer.musicplayer`: Allows access to music player commands.
* `composer.musicplayer.others`: Allows use of -p flag.


