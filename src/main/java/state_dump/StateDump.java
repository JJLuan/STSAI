package state_dump;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@SpireInitializer
public class StateDump implements OnCardUseSubscriber, PostBattleSubscriber, PostEnergyRechargeSubscriber, OnStartBattleSubscriber {
    private ArrayList<AbstractCard> cardList;
    public static final Logger logger = LogManager.getLogger(StateDump.class.getName());

    public StateDump() {
        BaseMod.subscribe(this);
        cardList = new ArrayList<>();
    }

    public static void initialize() {
        new StateDump();
    }


    public static void DumpBattleState() {
        AbstractPlayer player = AbstractDungeon.player;
        logger.info("Cards in draw pile: ");
        for (AbstractCard c : player.drawPile.group) {
            logger.info(c.name);
        }
    }

    @Override
    public void receiveCardUsed(AbstractCard c) {
        cardList.add(c);
    }

    @Override
    public void receivePostBattle(AbstractRoom battleRoom) {
        logger.info("Cards played:");
        for (AbstractCard c : cardList){
            logger.info(c);
        }

        cardList.clear();
    }

    @Override
    public void receivePostEnergyRecharge() {
        logger.info("turn start!");
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        logger.info("battle start");
        DumpBattleState();
    }
}
