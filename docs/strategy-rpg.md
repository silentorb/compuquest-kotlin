# Design Challenges for a Strategy RPG

## Traditional Strategy Games

* Most strategy games are PvP
* Most strategy games provide players with mirrored choices
  * Even faction selection is a mirrored choice
* In Euro strategy games this is further mirrored by being a competition for who can acquire the most points, which makes much of resource balancing relative and flexible

## Role Playing Games

* Role playing games are essentially PvE
* PvE does not support mirrored choices
* PvE is primarily about reaching objectives and surviving
* That makes balancing resources significantly more difficult and brittle
* Most PvE games have minimal macro gameplay
  * Usually the main macro game RPGs have is character building, which is usually likewise flawed in how it expects the player to make uninformed long-term decisions and only learn the fruit of their decisions in the late game
    * This flaw is minimized in Roguelites, which are designed around many shorter play throughs instead of investing 40+ hours into a single play through

## Non Linear Gameplay

* CompuQuest has a secondary challenge with its genre integration: not only is it an RPG, it is a non-linear RPG

* No matter how non-linear the game, it still is essentially a branching tree graph of possibilities (even when those possibilities are incomprehensible)

* The problem with non-linear games is they naturally become unbounded and impossible to comprehensively design and develop, leading to glitchy and imbalanced gameplay

* Careful constraints need to be placed on non-linear gameplay to keep it manageable

* One of the most common constraints placed upon non-linear gameplay is to rely upon commutative operations

  * The problem with this technique is it turns non-linear gameplay into an illusion: the player can accomplish the steps toward an objective in any order but the order does not matter
  * One example of this is a door that requires three keys, with each key placed in a different realm
    * The player can explore each realm in any order, but the order has little impact on the gameplay and is mainly just a player preference
    * The degree of cumulativeness in this case can vary, for example:
      * If the player can gain experience and upgrades, the last realm explored will be easier
        * This is minimized and sometimes reversed by enemy auto scaling
      * A variation of the previous example is when different items can be found in each realm which support the player's efforts in the other realms
  * One common commutative operation is gaining experience and levels, be it through grinding or quests

* Some roguelites like Slay the Spire have a very tight structure with tiered progression and branching paths

  * A structure like that makes it easier to balance the macro systems

* In general I don't like such an abstract form of branching in games

  * Gem Star subverted that by *feeling* non-linear and featuring the larger meta-game of multiple passes going down different routes
  * While I don't like that format, games like Slay the Spire can be so well executed that I can forgive the format and enjoy the game
    * Though in the back of my mind the abstraction still loses me a little
    * While I can become engrossed by the mechanics of the game, I'm never immersed

* However, my main complaint is when the branching is so explicitly abstract, and I'm not sure I mind if the branching is hidden behind a more concrete and organic UI

* One other note on Slay the Spire is its most pivotal and macro choices are generally restrained to a choice between 2-4 options

  * The more free-form and non-linear format of the battles has far less macro impact
    * The primary macro impact of a battle is how much health you have at the end of it
    * The secondary impact is what potions you have consumed (and don't have anymore) and the rare, permanent in-battle effects

  ## Conclusions

  * Non-linear gameplay is expensive and should be used sparingly
    * It is the same as randomness, the source of so many dangers I faced when pursuing procedurally generated content
    * It's possible CompuQuest can still be very non-linear, but non-linear gameplay needs to be something I carefully inject into the gameplay, not something I start with as a base and then try to control as an after-thought
      * That is the same rule I finally employed with randomness