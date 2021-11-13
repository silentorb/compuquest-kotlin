# CompuQuest Design

## Description

CompuQuest is a strategy role playing game.  It is also:

* Party-based
* 3D
* Real-time
* Rogue-lite

## High Level Requirements

* Simple UI with minimal micromanagement
* Macro strategy
* Capture a particular retro feeling
* Strong sense of adventure
* Some combat but also lots of non-combat systems
* Each playthrough should be significantly different independent of the player's choices, but only different in easy-to-implement ways
* Sandbox
* Surreal
* Sublime wonder
* Humorous / Wacky / Satirical

## Initial Core Concepts

### Essential Concepts

* RPG
* First person
* Real time
* Simple graphics
* Relatively simple systems for equipment, abilities, and character creation
* Merged race and class
  * Instead there will be a tag system and each character template will have any number of tags defining and categorizing that character
* No procedurally generated architecture
  * The world will be static, though I'm open to the possibility of different versions of locations getting swapped out based on game state
* Relatively small, dense world with each zone crammed next to each other
* Some key aspects of the world can be randomized each playthrough
  * This may be kept to a minimum

### Fairly Certain Concepts

* Most resources are macro
  * Mana and ammo won't be resources that each party member personally possesses in small quantities and needs to regain it between battles
  * Instead, they will be larger resources that are more of a long-term concern
  * The issue with conserving mana is not whether you will have enough mana for that battle, but how much resources you are expending in the big picture
    * Every attack is effectively costing you money
* Support for negative player resources
  * It's like an abstraction for going into debt

### Less Certain Concepts

* Within a playthrough, most state is persistent but some state is ephemeral
* Quests are like recipe ingredients: Complete any recipe to win the game
* Income mostly through quests
* Quests need to be carefully selected for synergy and effectively form a campaign, similar to Ticket to Ride
* Some quests have prerequisites and are neither available nor visible until the prerequisites are met
  * A quest can depend on another quest
* Available quests are only created at the start of a playthrough
* Zone occupation
  * Different major zones can be owned by different factions, resulting in different types of roaming mobs
  * Similar to Risk
* World gets more dangerous over time
  * It's a race to beat the game

#### Party Based Concepts

* Four member party
* Party members share the same player body
* Automatic, Molyneux-style member AI actions
  * You do not directly control your party members
* Missile attacks always hit
  * There is no spatial dodging, though there could be an RNG-based dodge ability
* Any time the player is hit or effected by an attack or negative ability, the player is temporarily slowed down
  * This will reduce the viability of the player just running past threats and force the player to engage them
* The player will be able to hire any number of heroes but can only directly use four of them at a time
  * The others can be sent on missions that abstractly progress state over time
    * Gain experience
    * Generate expenses and revenue
    * Potentially affect other more particular aspects of the world
  * Heroes have wages and will leave if they aren't paid
  * Heroes can be rehired but may not always be available after they leave
* No equipment, or at least no distinction from abilities
* Characters can be resurrected but it is expensive
* Characters gaining experience
  * Characters gain the most experience from dying

## Major Systems

* Combat
* Party
* Team
* Open World loading/unloading
* Economies
* Time Lapse
* Enemy AI
  * Can be very basic
* Character Definition
* Enemy population
* World map

## Minor Systems

* Teleporters/Portals
* Member death
  * Resurrection?
  * Obliteration?
* Character growth

## Composition

### Characters

#### Certain

* Janitor
* Ninja
* Alien
* Skeleton
* Robot
* Gorilla
* Wizard
* Secret Agent
* Viking
* Elf
* Kid (Boy)
* Zombie

#### Less Certain

* Programmer
* Venus Fly Trap
* Mad Scientist
* Wolf Man

#### NPCs

* Photographer/Journalist
* Teleporting Wizard

### Locations

#### More Certain

* Neighborhood
* Didopolis (Cyber city)
* Pipe Maze
* Castle
* Pizza Parlor / Burger Joint
* Abandoned House (from Adventure and Zork)
* Mansion
* Graveyard
* Jungle
* Office building overtaken by demonic forces
* BBQ Party
* Desert

#### Less Certain

* Dungeon
* Caves
* Computer Underworld
* Space Station/Ship
* Fairy Forest
* Swamp
* Small planet
* Underwater
* Arctic
* Subway
* Mystic Mountain

## Inspirations

* Might and Magic series
* Wizardry series
* Ticket to Ride
* DOOM
* Build Engine games
* Dark Souls
* Populous
* NES Castlevania games
* Arctic Adventure
* Robomaze III
* Adventure (Colossal Cave)
* Pokémon
* Secret Agent
* Monster Bash
* Neverwinter Nights
* Space Quest
* The Catacomb Abyss
* Duke Nukem I
* Tales of Maj'Eyal
* Commander Keen series
* Hexen
* Paganitzu
* Black Future 88
* Shadow Caster
* Wolfenstein 3D
* Megazeux
* Heartlight
* Rift Wizard
* Crawl
* You Have to Win the Game
* Haque
* Bloom
* Zombies Ate My Neighbors
* Arthurian Legends
* Vampire : The Masquerade
