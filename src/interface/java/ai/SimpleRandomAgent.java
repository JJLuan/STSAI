package ai;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SimpleRandomAgent {
    public static final Logger logger = LogManager.getLogger(SimpleRandomAgent.class.getName());
    public static boolean fullControl = true;

    public static AbstractMonster target = null;


    public static void playFirstPlayable(AbstractPlayer p) {
        if (!AbstractDungeon.actionManager.isEmpty()) {
            logger.info("action manager is not empty, no action taken");
            return;
        }

        if (p.hand.size() == 0) {
            logger.info("empty hand");
            return;
        }

        if (EnergyPanel.getCurrentEnergy() == 0) {
            AbstractDungeon.overlayMenu.endTurnButton.disable(true);
            return;
        }

        boolean picked = false;
        int i = 0;
        while (!picked && i < p.hand.size()) {
            p.hoveredCard = p.hand.group.get(i++);
            if (p.hoveredCard.name.equals("Strike")) {
                p.hoveredCard.target = AbstractCard.CardTarget.ENEMY;
                //AIMonsterField.aiMonsterTarget.set(p, AbstractDungeon.getRandomMonster());
                target = AbstractDungeon.getRandomMonster();
                picked = true;
            }
            else if(p.hoveredCard.name.equals("Defend")){
                p.hoveredCard.target = AbstractCard.CardTarget.NONE;
                target = null;
                picked = true;
            }
        }
        if (picked){
            try {
                if (p.hoveredCard != null && AbstractDungeon.getMonsters() != null)
                    logger.info(String.format("attempting to play %s targeting %s in position %d",
                            (p.hoveredCard == null ? null : p.hoveredCard.target),
                            target,
                            (AbstractDungeon.getMonsters() == null ? null : AbstractDungeon.getMonsters().monsters.indexOf(target))));

                Method m = p.getClass().getSuperclass().getDeclaredMethod("playCard");
                m.setAccessible(true);
                m.invoke(p);


                //last_hand_size = p.hand.size();

            } catch (NoSuchMethodException e) {
                logger.error("No such method playCard");
            } catch (InvocationTargetException e) {
                logger.error("failed to invoke playCard");
            } catch (IllegalAccessException e) {
                logger.error("permissions bad for playCard");
            }
        }
        else {
            logger.info("couldn't pick a card, ending turn");
            AbstractDungeon.overlayMenu.endTurnButton.disable(true);
        }
    }

    public static ArrayList<AbstractCard> getListOfCardsInHand(AbstractPlayer player){
        ArrayList<AbstractCard> cards = new ArrayList<>();

        for (AbstractCard card : player.hand.group){
            logger.info(String.format("Player Hand:\t%s", card.name));
        }

        return player.hand.group;
    }

    public static void dumpInfo(){
        logger.info(String.format("Number of cards:\t%d", CardLibrary.getAllCards().size()));
        int totalNumRelics = 0;
        totalNumRelics += RelicLibrary.blueList.size() + RelicLibrary.greenList.size() + RelicLibrary.redList.size();
        totalNumRelics += RelicLibrary.commonList.size() + RelicLibrary.rareList.size() + RelicLibrary.bossList.size();

        logger.info(String.format("Number of relics:\t%d", totalNumRelics));
        logger.info(String.format("Number of potions:\t%d", PotionHelper.potions.size()));
    }
}
