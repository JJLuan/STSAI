package state;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

@SpireInitializer
public class GameListener implements OnCardUseSubscriber, PostBattleSubscriber, PostEnergyRechargeSubscriber, OnStartBattleSubscriber, PostDungeonInitializeSubscriber {
    private ArrayList<AbstractCard> cardList;
    public static final Logger logger = LogManager.getLogger(GameListener.class.getName());

    private static MappedByteBuffer mmap;
    private static int proto_size;

    private static ByteArrayOutputStream out;

    public GameListener() {
        BaseMod.subscribe(this);
        cardList = new ArrayList<>();
        Path f = Paths.get(System.getProperty("user.dir") + "/state.data");
        logger.info("opening memory map at: "+f.toString());
        try (FileChannel fc = (FileChannel) Files.newByteChannel(f, EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE))) {
            f.toFile().createNewFile();
            mmap = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1048576);

        } catch (IOException e) {
            logger.error("problem trying to map to file:");
            logger.catching(e);
        }
        if (mmap != null) {
            byte[] buf = StateHandler.TestState().toByteArray();
            proto_size = buf.length;
            mmap.put(buf);
        }
    }

    public static void initialize() {
        new GameListener();
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

    @Override
    public void receivePostDungeonInitialize() {
        logger.info(String.format("reading mmap, %d bytes", proto_size));
        try {
            byte[] buf = new byte[proto_size];
            mmap.position(0);
            mmap.get(buf);
            logger.info(Arrays.toString(buf));
            StateProtos.State s  = StateProtos.State.parseFrom(buf);
            logger.info(s.toString());
        } catch (InvalidProtocolBufferException e) {
            logger.error("bad protocol buffer");
            logger.catching(e);
        }
    }
}
