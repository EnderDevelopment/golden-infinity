# Minecraft Manifestation Plugin

Golden Infinity Manifestation is a balanced Minecraft plugin for Paper 1.21.11 that detects a player movement pattern resembling an infinity symbol, summons a temporary golden particle manifestation, displays elegant messages above the hotbar, and grants limited wishes through commands or chat.

## Install

1. Install Paper 1.21.11 with Java 21.
2. Place the project files in a Maven project.
3. Build the plugin:

   ```bash
   mvn package
   ```

4. Copy `target/plugin.jar` into the server's `plugins` folder.
5. Restart the server.
6. Confirm that the plugin loads successfully in the server console.

## Use

1. Join the server and move in an infinity-like pattern across the ground.
2. Complete the movement pattern near its starting point to summon the Manifestation.
3. The Manifestation remains available for approximately 60 seconds.
4. Make a wish through chat by mentioning one of the supported requests:
   - `food`
   - `arrows`
   - `experience`
   - `gold`
5. You can also summon it directly with:

   ```text
   /manifestation call
   ```

6. You can make a specific wish with:

   ```text
   /manifestation wish food
   /manifestation wish arrows
   /manifestation wish experience
   /manifestation wish gold
   ```

7. Each player must wait 30 minutes between granted wishes.
8. The Manifestation displays elegant responses in the action bar above the hotbar.
9. While summoned, it appears through gold dust particles arranged in a circle with soul particles and a central golden aura.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/15614016-59f5-4534-a363-6d554bbbfa84?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=15614016-59f5-4534-a363-6d554bbbfa84
