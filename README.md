# STSAI
    mvn package
to build

mvn spec is a mess

take the resulting jar and move it to C:\Program Files (x86)\Steam\steamapps\common\SlayTheSpire\mods or your equivalent

check basemod wiki for environment setup and dependencies

# State enumeration (always)
- decklist
- current health
- maximum health
- current floor
- all possible routes to act boss
- act number/ascension
- relics owned
- relics seen
- potions in inventory

# State enumeration (Battle)
- monster types (up to 6)
- monster intents (up to 6)
- player/monster buffs/debuffs/orbs/powers/innate abilities (ex. malleable)
- cards in draw pile (what about Frozen Eye?)
- cards in discard
- cards exhausted
- cards in hand
- energy remaining

# State enumeration (shop)
TODO

# State enumeration (Events)
- Choice ids 1-4
- special events need specific handling (memory game)

# Action enumeration
TODO
