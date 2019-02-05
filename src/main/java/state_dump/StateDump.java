package state_dump;
import basemod.BaseMod;
import basemod.interfaces.OnCardUseSubscriber;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostEnergyRechargeSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@SpireInitializer
public class StateDump implements OnCardUseSubscriber, PostBattleSubscriber, PostEnergyRechargeSubscriber {
    private ArrayList<AbstractCard> cardList;
    public static final Logger logger = LogManager.getLogger(StateDump.class.getName());

    public StateDump() {
        BaseMod.subscribe(this);
        cardList = new ArrayList<>();
    }

    public static void initialize() {
        new StateDump();
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
}
