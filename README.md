# Oneblock
<a href="https://www.spigotmc.org/resources/oneblock.83215/"><img src="https://i.ibb.co/xCRdNdM/ob.png" width="200" alt="spigot page"></a>by MrMarL

<br>OneBlock minigame plugin.
<br>Main Commands:
<br>▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
<br>▌/ob set - set the first block /ob set 1 time for the admin then just /ob join and the island will be created by itself ...
<br>▌/ob set 500 - set the first block and setting the distance of 500 blocks between the islands.
<br>▌/ob join - join a free block(x+100)
<br>▌/ob autojoin true - when players connect to the world with oneblock mode, they will automatically join it.
<br>▌/ob protection true - does not allow players to get out of the island)
<br>▌/ob invite 'playername' - invites the player to the island.
<br>▌# If you just want to visit, use /tpa...
<br>▌/ob accept - to accept an invitation.
<br>▌/ob IDreset - removes you from the player base. you will be able to create a new island.
<br>▌/ob frequency "value" //less is better but lower tps (6 - 8 is recommend)
<br>▌# If you set it too high, players will die from falling)
<br>▌/ob lvl_mult 5 - The number of blocks that must be broken to get a new level is calculated using the formula:
<br>▌ 16+level now*level multiplier
<br>█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

<br>Island Commands:
<br>▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
<br>▌/ob islands true - Islands for new players.
<br>▌/ob islands set_my_by_def - sets your island as default for new players.
<br>▌/ob islands default - resets the custom island and sets the default.
<br>▌/ob island_rebirth true - Rebirth of the player on the island
<br>█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

<br>Config Commands:
<br>▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
<br>▌/ob reload blocks.yml - reload...
<br>▌/ob listlvl - displays a list of levels from blocks.yml
<br>▌/ob listlvl 12 -displays a list of blocks added at the level from blocks.yml
<br>▌/ob reload chests.yml - reload chests?...
<br>█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

<br>Other Commands:
<br>/ob setleave
<br>/ob leave
<br>/ob chat_alert - display the level in the chat
<br>/ob Progress_bar color RED - set Progress_bar color
<br>/ob Progress_bar true - on Progress_bar
<br>/ob Progress_bar false - off Progress_bar
<br>/ob Progress_bar level - Progress_bar level mode
<br>/ob Progress_bar settext <text> - Progress_bar text change
<br>/ob setlevel "nick" 14 - set ob level
<br>/ob clear 'player' - reset the player ob level and remove his island
<br>/ob ver

<br>PlaceholderAPI:
<br>%OB_ver%
<br>%OB_lvl%
<br>%OB_break_on_this_lvl%
<br>%OB_need_to_lvl_up%
<br>%OB_lvl_name%
<br>You can use placeholders in the progress bar text!</br>
