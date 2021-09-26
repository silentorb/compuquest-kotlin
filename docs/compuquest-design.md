# CompuQuest Design

## High Level Requirements

* Simple UI with minimal micromanagement
* Capture a particular retro feeling
* Strong sense of adventure
* Some combat but also lots of non-combat systems
* Each playthrough should be significantly different independent of the player's choices, but only different in easy-to-implement ways
* Sandbox
* Surreal
* Sublime wonder
* Humorous / Wacky

## Inspirations

* Might and Magic series
* Wizardry series
* DOOM
* Build Engine games
* Tales of Maj'Eyal
* Populous
* NES Castlevania games
* Arctic Adventure
* Robomaze III
* Adventure (Colossal Cave)
* Pokémon
* Secret Agent
* Monster Bash
* Black Future 88
* Neverwinter Nights
* Space Quest
* The Catacomb Abyss
* Duke Nukem I
* Commander Keen series
* Hexen
* Paganitzu
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

## Initial Core Concepts

### Essential Concepts

* RPG
* First person
* Real time
* Four member party
* Party members share the same player body
* Simple graphics
* Relatively simple systems for equipment, abilities, and character creation
* Merged race and class
  * Instead there will be a tag system and each character template will have any number of tags defining and categorizing that character
* No procedurally generated architecture
  * The world will be static, though I'm open to the possibility of different versions of locations getting swapped out based on game state
* Relatively small, dense world with each zone crammed next to each other

### Fairly Certain Concepts

* Automatic, Molyneux-style member AI actions
  * You do not directly control your party members
* Player stances that influence member behavior
  * The player can change stances at any time
* Most any enemy can be a party member
* Missile attacks always hit
  * There is no spatial dodging, though there could be an RNG-based dodge ability
* Any time the player is hit or effected by an attack or negative ability, the player is temporarily slowed down
  * This will reduce the viability of the player just running past threats and force the player to engage them
* The player will be able to hire any number of heroes but can only directly use four of them at a time
  * The others can be sent on missions that abstractly progress state over time
    * Gain levels
    * Generate expenses and revenue
    * Potentially affect other more particular aspects of the world
  * Heroes have wages and will leave if they aren't paid
  * Heroes can be rehired but may not always be available after they leave
* The player will have the option to setup bases of operation
  * Certain locations in the world will be defined as possible base locations
  * The player can change their base with some hassle
* Most resources are macro
  * Mana and ammo won't be resources that each party member personally possesses in small quantities and needs to regain it between battles
  * Instead, they will be larger resources that are more of a long-term concern
  * The issue with conserving mana is not whether you will have enough mana for that battle, but how much resources you are expending in the big picture
    * Every attack is effectively costing you money
* Lots of ways that the player feels a sense of growth
* Within a playthrough, most  state is persistent but some state is ephemeral
* A simulation that combines both micro and macro layers
  * Though the game is increasingly looking like it will actually be more macro than micro
* Some key aspects of the world can be randomized each playthrough
  * This may be kept to a minimum
* Support for negative player resources
  * It's like an abstraction for going into debt

* Income mostly through quests
* Quests need to be carefully selected for synergy and effectively form a campaign, similar to Ticket to Ride

### Less Certain Concepts

* World gets more dangerous over time
  * It's a race to beat the game
* Zone occupation
  * Different major zones can be owned by different factions, resulting in different types of roaming mobs
  * Similar to Risk
* 3D World Map

## Design Concerns

Everything in [Less Certain Concepts](#less-certain-concepts) is a concern

* Is the automatic combat going to not be engaging enough?
  * Is it going to be frustrating?
  * Are the attempts to indirectly control the members end up feeling awkward and over-complicated for little benefit?
  * These concerns also apply specifically to the stance system, which is an unproven idea and may not end up working

## Design Questions

* How do you win?
  * I'm considering possibilities for minimizing win/loss being a boolean threshold and instead having variations of success and failure
* Will there be inventory
  * If so, how will it be handled, particularly with swapping out characters?

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

#### NPCs

* Photographer/Journalist
* Teleporting Wizard

### Locations

#### More Certain

* Neighborhood
* Didopolis (Cyber city)
* Pipe Maze
* Castle
* Mystic Mountain
* Pizza Parlor / Burger Joint
* Abandoned House (from Adventure and Zork)
* Haunted mansion

#### Less Certain

* BBQ Party
* Dungeon
* Caves
* Computer Underworld
* Space Station/Ship
* Fairy Forest
* Jungle
* Swamp
* Office building overtaken by demonic forces
* Small planet
* Desert
* Underwater
* Arctic
* Subway

### Base Locations

* Didopolis
* Haunted mansion
* Castle
* Pizza/Burger Joint ?