package me.strobe.Extensions;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import com.shampaggon.crackshot.CSUtility;
import de.tr7zw.nbtapi.plugin.NBTAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is Main in Project: (Gta-Core) : But you already knew that
 *
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/25/2022, 8:44 AM
 * Last Edit     : 2/25/2022, 8:44 AM (Update Me!)
 * Time to Write : (Rough Estimate)
 * <p>
 * (Class Description)
 */
public class Main extends JavaPlugin {

    @Getter private static Main main;
    @Getter private static CSDirector csDirector;
    @Getter private static CSUtility csUtility;
    @Getter private static CSMinion csMinion;
    @Getter private static NBTAPI nbtapi;


    public Main(){
        main = this;
        csUtility = new CSUtility();
        csDirector = csUtility.getHandle();
        csMinion = csDirector.csminion;
        nbtapi = NBTAPI.getInstance();
    }

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable(){

    }



}
