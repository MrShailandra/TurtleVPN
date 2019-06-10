package tech.turtlesoftsol.TurtleVPN;
import android.content.Intent;

import com.daimajia.androidanimations.library.Techniques;
import tech.turtlesoftsol.TurtleVPN.R;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;
/**
 * Created by Shailandra Rajput on 04-06-2019.
 */
public class SplashScreen extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {


        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.bg_screen1); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP


        //Customize Logo
        configSplash.setLogoSplash(R.drawable.ic_launcher); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.SlideInDown); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Path
        // configSplash.setPathSplash(SyncStateContract.Constants.DROID_LOGO); //set path String
        configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
        configSplash.setAnimPathStrokeDrawingDuration(1000);
        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
        configSplash.setPathSplashStrokeColor(R.color.colorAccent); //any color you want form colors.xml
        configSplash.setAnimPathFillingDuration(3000);
        configSplash.setPathSplashFillColor(R.color.bg_screen1); //path object filling color


        //Customize Title
        configSplash.setTitleSplash("TurtleVPN"); //change your app name here
        configSplash.setTitleTextColor(R.color.mapLine);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.RollIn);
        configSplash.setTitleFont("fonts/Pacifico.ttf"); //provide string to your font located in assets/fonts/

    }

    @Override
    public void animationsFinished() {
        Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
        startActivity(i);

        //transit to another activity here
        //or do whatever you want
    }
}

