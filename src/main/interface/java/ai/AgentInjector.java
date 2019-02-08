package ai;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AgentInjector {
    public static final Logger logger = LogManager.getLogger(AgentInjector.class.getName());

    //@SpirePatch(
    //        clz = AbstractPlayer.class,
    //        method = SpirePatch.CONSTRUCTOR
    //)
    //public static class AgentInit {
    //    public static void Postfix(AbstractPlayer __instance, String name, AbstractPlayer.PlayerClass setClass) {
    //    }
    //}

    @SpirePatch(
            clz = GameActionManager.class,
            method = "update"
    )
    public static class StartAgent {
        //@SpireInsertPatch(
        //        locator=Locator.class
        //)
        //public static void Insert(GameActionManager __instance) {
        //    if (__instance.phase == GameActionManager.Phase.WAITING_ON_USER) {
        //        logger.info("starting agent...");
        //        SimpleRandomAgent.playFirstPlayable(AbstractDungeon.player);
        //    }
        //}


        //public static class Locator extends SpireInsertLocator {
        //    public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
        //        Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameActionManager.class, "phase");

        //        int[] res = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        //        for (int i = 0; i < res.length; i++) {
        //            res[i] = res[i]+1;
        //        }
        //    }
        //}

        public static void Postfix(GameActionManager __instance) {
            if (__instance.phase == GameActionManager.Phase.WAITING_ON_USER
                    && !(AbstractDungeon.getCurrRoom().isBattleOver
                    || AbstractDungeon.getCurrRoom().isBattleEnding())
                    && AbstractDungeon.actionManager.isEmpty()) {
                logger.info("get agent action");
                SimpleRandomAgent.playFirstPlayable(AbstractDungeon.player);
            }
        }

        //public static ExprEditor Instrument() {
        //    logger.info("starting agent...");
        //    return new ExprEditor(){
        //        public void edit(MethodCall c) throws CannotCompileException {
        //            logger.info(c.getMethodName());
        //            if (c.getMethodName().equals("EnableEndTurnButton")) {
        //                logger.info("injecting agent...");
        //            }
        //        }
        //    };
        //}
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method="playCard"
    )
    public static class PlayAgentCard{
        public static void Prefix(AbstractPlayer __instance) {
            logger.info("playCard");
       }
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(FieldAccess f) throws CannotCompileException {
                    logger.info("Field access for "+f.getFieldName()+ ": "+f.getLineNumber());
                    if (f.getFieldName().equals("hoveredMonster")) {
                        logger.info(f.getEnclosingClass().getName());
                        f.replace("{$_ = ai.SimpleRandomAgent.target;}");
                    }

                }
                //AbstractDungeon.player.decreaseMaxHealth(1);

            };
        }
    }
}
