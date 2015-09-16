# Automatic miner for Minecraft

This application attempts to find an optimal algoritm for mining in minecraft,
specifically optimized for mining diamond ore.

## What it does

This application reads Minecraft levels and mines them. It contains an algorithm
that (kind of) behaves like an actual person would. It mines at certain levels
at a random point in the world and diggs out all ore found in tunnels.

The way the mining algorithm works is:

* The application picks a random starting point in the world and starts mining
  from there;
* From that point, towards positive zero, it mines a one wide, two high tunnel;
* If it finds either ore or air either in the mined block or directly adjacent
  to the mined block, it recurses into that block;
* This goes on until all air blocks or ore blocks are exchausted.

The application itself tries a few algorithms and gathers data for it. Graphs for
this data are shown below.

## The data

The reason I wrote this application is that I wanted to verify the common approach
for mining for diamond ore. This states that you should mine at a depth of 12
and create a new branch every four blocks. (Advises differ, but this is the one
I've been using for a while.)

The TL;DR of the results of this application is:

* Mining at 12 seems to be the best level, but mining at level 13 is going to
  cost you roughly 1 diamond ore block per 1000 mined blocks;
* Far more interesting is that having just three blocks between tunnels does
  not seem to be a good idea. Instead, the more space you leave between tunnels,
  the better. The difference between leaving three blocks between tunnels
  (the x+4 strategy in the diagrams) and five blocks (the x+6 strategy in the
  diagrams) is almost two blocks per 1000 mined blocks (at level 12).

The graphs below show details information. Per mined block type, the diagram
displays the number of blocks mined per 1000 mined blocks (i.e. tunnels 1000
blocks long). These samples are taken by executing the test in 10 random
points in the map and mining four adjacent tunnels.

![Diamond](http://i.imgur.com/XTh9GAG.png)

![Lava](http://i.imgur.com/Npsejpf.png)

![Dirt](http://i.imgur.com/v9DDeAB.png)

![Gold](http://i.imgur.com/iwKRiX4.png)

![Gravel](http://i.imgur.com/7w0LCif.png)

![Iron](http://i.imgur.com/XW64Hs0.png)

![Lapis](http://i.imgur.com/p1wM4xr.png)

![Redstone](http://i.imgur.com/Xs5K6DV.png)

![Stone](http://i.imgur.com/W0FaY6D.png)

![Coal](http://i.imgur.com/UmUzQLu.png)

## The code

The code for parsing the Minecraft world data is taken from [Ager](https://github.com/Zarkonnen/Ager).
Most of the functionality of that application has been stripped out and just
the code for reading levels is kept.

To use the application, you first need a world. The easiest way to do this is to
just create a new world and generate chunks in it. I've tested the application
with a world where 3000 blocks were generaed in the Z direction, flying over
in one pass. This is enough to get representative data.

On the first run, a cache file is created. Reading the world is slow and the
cache file only keeps the block types, only for Y 0 through 22. This cache file
is used to do the actual calculations on.

## Contributions

If you have ideas for algorithms you want to try out, please have a run at
the code and create a pull request if you'd like to see it added.
