package state_dump;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Agent {
    public static final Logger logger = LogManager.getLogger(Agent.class.getName());
    public static boolean temp = true;

    public static AbstractMonster target = null;

    public static void playFirstPlayable(AbstractPlayer p) {
        if (p.hand.size() == 0) {
            logger.info("empty hand");
            return;
        }

        boolean picked = false;
        int i = 0;
        while (!picked) {
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
        try {
            if (temp) {
                logger.info(p.getClass());
                Method m = p.getClass().getSuperclass().getDeclaredMethod("playCard");
                m.setAccessible(true);
                m.invoke(p);
                //temp = false;
            }
        }
        catch (NoSuchMethodException e){
            logger.error("No such method playCard");
        }
        catch (InvocationTargetException e){
            logger.error("failed to invoke playCard");
        }
        catch (IllegalAccessException e) {
            logger.error("permissions bad for playCard");
        }
    }
}
