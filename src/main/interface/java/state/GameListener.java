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
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
    private static FileChannel fc;
    private static FileLock fl;
    private static int proto_size;

    private static StateProto.State global_state;

    private static ByteArrayOutputStream out;

    public GameListener() {
        BaseMod.subscribe(this);
        cardList = new ArrayList<>();
        Path f = Paths.get(System.getProperty("user.dir") + "/global_state.data");
        logger.info("opening memory map at: "+f.toString());
        try {
            f.toFile().createNewFile();
            fc = (FileChannel) Files.newByteChannel(f, EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE));
            mmap = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1048576);

        } catch (IOException e) {
            logger.error("problem trying to map to file:");
            logger.catching(e);
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
        try (FileLock ignored = GameListener.fc.lock()) {
            logger.info("last known buf size "+proto_size);
            byte[] buf = new byte[proto_size];
            mmap.position(0);
            logger.info("reading from position "+mmap.position());
            mmap.get(buf);
            logger.info(Arrays.toString(buf));
            StateProto.State s  = StateProto.State.parseFrom(buf);
            logger.info(s.toString());
        } catch (InvalidProtocolBufferException e) {
            logger.error("bad protocol buffer");
            logger.catching(e);
        } catch (IOException e) {
            logger.error("ioexception trying to lock mmap");
            logger.catching(e);
        }




    }

    @Override
    public void receivePostEnergyRecharge() {
        logger.info("turn start!");
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        logger.info("battle start");

        ArrayList<StateProto.Card> deck = new ArrayList<>();
        for (AbstractCard ac : AbstractDungeon.player.masterDeck.group) {
            deck.add(StateProto.Card.newBuilder().setName(ac.name).build());
        }

        global_state = StateProto.State.newBuilder()
                .setNumRelics(AbstractDungeon.player.relics.size())
                .setDeck(StateProto.Deck.newBuilder()
                        .addAllC(deck)
                        .setSize(deck.size()))
                .build();

        try (FileLock ignored = GameListener.fc.lock()){
            if (mmap != null) {
                byte[] buf = global_state.toByteArray();
                proto_size = buf.length;
                logger.info("writing "+proto_size+" bytes");
                logger.info(Arrays.toString(buf));
                mmap.position(0);
                mmap.put(buf, 0, proto_size);
            }
        } catch (IOException e) {
            logger.error("ioexception trying to lock mmap");
            logger.catching(e);
        }
    }

    @Override
    public void receivePostDungeonInitialize() {
        logger.info(String.format("reading mmap, %d bytes", proto_size));
    }
}
