# Contributing to KoboshFolia

KoboshFolia is happy you're willing to contribute to our projects. We are usually
very lenient with all submitted PRs, but there are still some guidelines you
can follow to make the approval process go more smoothly.

## Understanding Patches

Unlike the API and its implementation, modifications to Minecraft source files
are done through patches. These patches/extensions are split into different
three different sets, which are:

- `sources`: Per-file patches to Minecraft classes;
- `resources`: Per-file patches to Minecraft data files;
- `features`: Larger feature patches that modify multiple Minecraft classes.

Because this entire structure is based on patches and git, a basic understanding
of how to use git is required. A basic tutorial can be found here:
<https://git-scm.com/docs/gittutorial>.

Assuming you have already forked the repository:

1. Clone your fork to your local machine;
2. Type `./gradlew applyPatches` in a terminal to apply the patches.
   On Windows, remove the `./` the beginning of `gradlew` commands;
3. cd into `paper-server` for server changes, and `paper-api` for API changes.
   **Only changes made in `koboshfolia-server/src/minecraft` have to deal with the patch system.**

`koboshfolia-server/src/minecraft` is not a git repositories in the traditional sense. Its
initial commits are the decompiled Minecraft source files. The per-file
patches are applied on top of these files as a single, large commit, which is then followed
by the individual feature-patch commits.

## Modifying (per-file) Minecraft patches

This is generally what you need to do when editing Minecraft files. Updating our
per-file patches is as easy as:

1. Making your changes;
2. Running `./gradlew fixupMinecraftSourcePatches` in the root directory;
3. If nothing went wrong, rebuilding patches with `./rb.sh`;

### Resolving rebase conflicts

If you run into conflicts while running `fixupMinecraftSourcePatches`, you need to go a more
manual route:

This method works by temporarily resetting your `HEAD` to the desired commit to
edit it using `git rebase`.

0. If you have changes you are working on, type `git stash` to store them for
   later;
   - You can type `git stash pop` to get them back at any point.
1. cd into `koboshfolia-server/src/minecraft/java` and run `git rebase -i base`;
   - It should show something like
     [this](https://gist.github.com/zachbr/21e92993cb99f62ffd7905d7b02f3159) in
     the text editor you get.
   - If your editor does not have a "menu" at the bottom, you're using `vim`.  
     If you don't know how to use `vim` and don't want to
     learn, enter `:q!` and press enter. Before redoing this step, do
     `export EDITOR=nano` for an easier editor to use.
1. Replace `pick` with `edit` for the commit/patch you want to modify (in this
   case the very first commit, `koboshfolia File Patches`), and
   "save" the changes;
1. Make the changes you want to make to the patch;
1. Run `git add .` to add your changes;
1. Run `git commit --amend` to commit;
1. Run `git rebase --continue` to finish rebasing;
1. Run `./rb.sh` in the root directory;

## Adding larger feature patches

Feature patches are exclusively used for large-scale changes that are hard to
track and maintain and that can be optionally dropped, such as the more involved
optimizations we have. This makes it easier to update KoboshFolia during Minecraft updates,
since we can temporarily drop these patches and reapply them later.

There is only a very small chance that you will have to use this system, but adding
such patches is very simple:

1. Modify `koboshfolia-server/src/minecraft` with the appropriate changes;
1. Run `git add .` inside that directory to add your changes;
1. Run `git commit` with the desired patch message;
1. Run `./rb.sh` in the root directory.

Your commit will be converted into a patch that you can then PR into KoboshFolia.

> ❗ Please note that if you have some specific implementation detail you'd like
> to document, you should do so in the patch message _or_ in comments.

## Modifying larger feature patches

One way of modifying feature patches is to reset to the patch commit and follow
the instructions from the [rebase section](#resolving-rebase-conflicts). If you
are sure there won't be any conflicts from later patches, you can also use the
fixup method.

### Fixup method

#### Manual method

1. Make your changes;
1. Make a temporary commit. You don't need to make a message for this;
1. Type `git rebase -i base`, move (cut) your temporary commit and
   move it under the line of the patch you wish to modify;
1. Change the `pick` to the appropriate action:
   1. `f`/`fixup`: Merge your changes into the patch without touching the
      message.
   1. `s`/`squash`: Merge your changes into the patch and use your commit message
      and subject.
1. Run `./rb.sh` in the root directory;
   - This will modify the appropriate patches based on your commits.

#### Automatic method

1. Make your changes;
1. Make a fixup commit: `git commit -a --fixup <hash of patch to fix>`;
   - If you want to modify a per-file patch, use `git commit -a --fixup file`
   - You can also use `--squash` instead of `--fixup` if you want the commit
     message to also be changed.
   - You can get the hash by looking at `git log` or `git blame`; your IDE can
     assist you too.
   - Alternatively, if you only know the name of the patch, you can do
     `git commit -a --fixup "Subject of Patch name"`.
1. Rebase with autosquash: `git rebase -i --autosquash base`.
   This will automatically move your fixup commit to the right place, and you just
   need to "save" the changes.
1. Run `./rb.sh` in the root directory. This will modify the
   appropriate patches based on your commits.

## Formatting

All modifications to Vanilla files should be marked. For historical reasons,
API and API-implementation contain a lot of these too, but they are no longer
required.

- You need to add a comment with a short and identifiable description of the patch:
  `// Kobosh start - <COMMIT DESCRIPTION>`
  - The comments should generally be about the reason the change was made, what
    it was before, or what the change is.
  - After the general commit description, you can add additional information either
    after a `;` or in the next line.
- Multi-line changes start with `// Kobosh start - <COMMIT DESCRIPTION>` and end
  with `// Kobosh end - <COMMIT DESCRIPTION>`.
- One-line changes should have `// Kobosh - <COMMIT DESCRIPTION>` at the end of the line.

> [!NOTE]
> These comments are incredibly important to be able to keep track of changes
> across files and to remember what they are for, even a decade into the future.

Here's an example of how to mark changes by KoboshFolia:

```java
entity.getWorld().dontBeStupid(); // Kobosh - Move away from beStupid()
entity.getFriends().forEach(Entity::explode);
entity.updateFriends();

// Kobosh start - Use plugin-set spawn
// entity.getWorld().explode(entity.getWorld().getSpawn());
Location spawnLocation = ((CraftWorld) entity.getWorld()).getSpawnLocation();
entity.getWorld().explode(new BlockPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()));
// Kobosh end - Use plugin-set spawn
```

We generally follow the usual Java style (aka. Oracle style), or what is programmed
into most IDEs and formatters by default. There are a few notes, however:

- It is fine to go over 80 lines as long as it doesn't hurt readability.  
  There are exceptions, especially in Spigot-related files
- When in doubt or the code around your change is in a clearly different style,
  use the same style as the surrounding code.
- Usage of the `var` keyword is discouraged, as it makes reading patch files a
  lot harder and can lead to confusion during updates due to changed return types.
  The only exception to this is if a line would otherwise be way too long/filled with
  hard to parse generics in a case where the base type itself is already obvious.

### Imports

When adding new imports to a Vanilla class, use the fully qualified class name
instead of adding a new import to the top of the file. If you are using a type a significant number of times, you
can add an import with a comment. However, if it's only used a couple of times, the FQN is preferred to prevent future
patch conflicts in the import section of the file.

```java
import net.minecraft.server.MinecraftServer;
// don't add import here, use FQN like below

public class SomeVanillaClass {
    public final org.bukkit.Location newLocation; // Kobosh - add location
}
```

### Nullability annotations

We are in the process of switching nullability annotation libraries, so you might need to use one or the other:

**For classes, we add or modifications to Vanilla Files**: Fields, method parameters and return types that are nullable should be marked via the
`@Nullable` annotation from `org.jspecify.annotations`. Whenever you create a new class, add `@NullMarked`, meaning types
are assumed to be non-null by default. For less obvious placing such as on generics or arrays, see the [JSpecify docs](https://jspecify.dev/docs/user-guide/).

**For other classes**: Keep using both `@Nullable` and `@NotNull` from `org.jetbrains.annotations`. These
will be replaced later.

### API checks

When performing API-related checks where an exception needs to be thrown under specific conditions, you should use the `Preconditions` class.

#### Checking Method Arguments

To validate method arguments, use `Preconditions#checkArgument`. This will throw an `IllegalArgumentException` if the condition is not met.

> Don't use Preconditions#checkNotNull, as it throws a NullPointerException, which makes it harder to determine whether the error was caused by an internal issue or invalid arguments.

ex:

```java
@Override
public void sendMessage(Player player, Component message) {
    Preconditions.checkArgument(player != null, "player cannot be null");
    Preconditions.checkArgument(player.isOnline(), "player %s must be online", player.getName());
    Preconditions.checkArgument(message != null, "message cannot be null");
    // rest of code
}
```

#### Checking Object State

To validate the state of an object inside a method, use `Preconditions#checkState`. This will throw an `IllegalStateException` if the condition is not met.
ex:

```java
private Player player;

@Override
public void sendMessage(Component message) {
    Preconditions.checkArgument(message != null, "message cannot be null");
    Preconditions.checkState(this.player != null, "player cannot be null");
    Preconditions.checkState(this.player.isOnline(), "player %s must be online", this.player.getName());
    // rest of code
}
```

## Access Transformers

Sometimes, Vanilla code already contains a field, method, or type you want to access
but the visibility is too low (e.g. a private field in an entity class). Paper can use access transformers
to change the visibility or remove the final modifier from fields, methods, and classes. Inside the `build-data/paper.at`
file, you can add ATs that are applied when you `./gradlew applyPatches`. You can read about the format of ATs
[here](https://mcforge.readthedocs.io/en/latest/advanced/accesstransformers/#access-modifiers).

## Configuration files

To use a configurable value in your patch, add a new field in either the
`GlobalConfiguration` or `WorldConfiguration` classes (inside the
`io.papermc.paper.configuration` package). Use `GlobalConfiguration` if a value
must remain the same throughout all worlds, or the latter if it can change
between worlds. World-specific configuration options are preferred whenever
possible.

### Example

This is adding a new miscellaneous setting that doesn't seem to fit in other categories.
Try to check and see if an existing category (inner class) exists that matches
whatever configuration option you are adding.

```java
public class GlobalConfiguration {
    // other sections
    public class Misc extends ConfigurationPart {
        // other settings
        public boolean lagCompensateBlockBreaking = true;
        public boolean useDimensionTypeForCustomSpawners = false;
        public int maxNumOfPlayers = 20; // This is the new setting
    }
}
```

You set the type of the setting as the field type, and the default value is the
initial field value. The name of the setting defaults to the snake-case of the
field name, so in this case it would be `misc.max-num-of-players`. You can use
the `@Setting` annotation to override that, but generally just try to set the
field name to what you want the setting to be called.

#### Accessing the value

If you added a new global config value, you can access it in the code just by
doing

```java
int maxPlayers = GlobalConfiguration.get().misc.maxNumOfPlayers;
```

Generally for global config values you will use the fully qualified class name,
`io.papermc.paper.configuration.GlobalConfiguration` since it's not imported in
most places.

If you are adding a new world config value, you must have access to an instance
of the `net.minecraft.world.level.Level` which you can then access the config by doing

```java
int maxPlayers = level.paperConfig().misc.maxNumOfPlayers;
```

#### Documentation

When adding or removing a config option, you should open a pull request in our [documentation repository](https://github.com/PaperMC/docs)
to keep it in sync and accurate. If you're unsure whether your original change will be accepted, then it's fine to wait with making the documentation
pull request until a maintainer has reviewed your changes. Once everything is done, the documentation pull request will be merged at the same time
that your original pull request is.

## Testing API changes

### Using the Paper Test Plugin

The Paper project has a `test-plugin` module for easily testing out API changes
and additions. To use the test plugin, enable it in `test-plugin.settings.gradle.kts`,
which will be generated after running Gradle at least once. After this, you can edit
the test plugin, and run a server with the plugin using `./gradlew runDev` (or any
of the other Paper run tasks).

### Publishing to Maven local (use in external plugins)

To build and install the Paper APIs and Server to your local Maven repository, do the following:

- Run `./gradlew publishToMavenLocal` in the base directory.

If you use Gradle to build your plugin:

- Add `mavenLocal()` as a repository. Gradle checks repositories in the order they are declared,
  so if you also have the Paper repository added, put the local repository above Paper's.
- Make sure to remove `mavenLocal()` when you are done testing, see the [Gradle docs](https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:case-for-maven-local)
  for more details.

If you use Maven to build your plugin:

- If you later need to use the Paper-API, you might want to remove the jar
  from your local Maven repository.  
  If you use Windows and don't usually build using WSL, you might not need to
  do this.

## Frequently Asked Questions

### My commit doesn't need a build, what do I do?

Quite simple: You add `[ci skip]` to the start of your pull request title.

This case most often applies to changes to files like `README.md`, this very
file (`CONTRIBUTING.md`), the `LICENSE.md` file, and also small updates to Javadocs or other documentation.

Do _not_ add [ci skip] to the start of your individual commit subjects, as your
pull request will not be mergeable until a new commit is added due to status check requirements.

### Patching and building is _really_ slow, what can I do?

This only applies if you're running Windows. If you're running a prior Windows
release, either update to Windows 10/11 or move to macOS/Linux/BSD.

In order to speed up patching process on Windows, it's recommended you get WSL 2.
This is available in Windows 10 v2004, build 19041 or higher. (You can check
your version by running `winver` in the run window (Windows key + R)). If you're
using an out-of-date version of Windows 10, update your system with the
[Windows 10 Update Assistant](https://www.microsoft.com/en-us/software-download/windows10) or [Windows 11 Update Assistant](https://www.microsoft.com/en-us/software-download/windows11).

To set up WSL 2, follow the information here:
<https://learn.microsoft.com/en-us/windows/wsl/install>

You will most likely want to use the Ubuntu apps. Once it's set up, install the
required tools with `sudo apt-get update && sudo apt-get install $TOOL_NAMES
-y`. Replace `$TOOL_NAMES` with the packages found in the
[requirements](#requirements). You can now clone the repository and do
everything like usual.

> ❗ Do not use the `/mnt/` directory in WSL! Instead, mount the WSL directories
> in Windows as described here:
> <https://learn.microsoft.com/en-us/windows/wsl/filesystems#view-your-current-directory-in-windows-file-explorer>
