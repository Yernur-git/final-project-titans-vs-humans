package utils;

import javax.sound.sampled.Clip;

public class SoundManager {
    private static SoundManager instance;

    private Clip backgroundMusic;
    private Clip placeUnitSound;
    private Clip titanAttackSound;
    private Clip humanAttackSound;
    private Clip titanDeathSound;
    private Clip trapTriggerSound;

    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

    private SoundManager() {
        backgroundMusic = ResourceLoader.loadSound("background.wav");
        placeUnitSound = ResourceLoader.loadSound("place_unit.wav");
        titanAttackSound = ResourceLoader.loadSound("titan_attack.wav");
        humanAttackSound = ResourceLoader.loadSound("human_attack.wav");
        titanDeathSound = ResourceLoader.loadSound("titan_death.wav");
        trapTriggerSound = ResourceLoader.loadSound("trap_trigger.wav");
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        } else if (enabled && backgroundMusic != null && !backgroundMusic.isRunning()) {
            playBackgroundMusic();
        }
    }

    public void setSfxEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
        if (!enabled) {
            stopAllSfx();
        }
    }

    public void playBackgroundMusic() {
        if (musicEnabled && backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void playPlaceUnitSound() {
        playSound(placeUnitSound);
    }

    public void playTitanAttackSound() {
        playSound(titanAttackSound);
    }

    public void playHumanAttackSound() {
        playSound(humanAttackSound);
    }

    public void playTitanDeathSound() {
        playSound(titanDeathSound);
    }

    public void playTrapTriggerSound() {
        playSound(trapTriggerSound);
    }

    private void playSound(Clip clip) {
        if (sfxEnabled && clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void stopAllSfx() {
        stopClip(placeUnitSound);
        stopClip(titanAttackSound);
        stopClip(humanAttackSound);
        stopClip(titanDeathSound);
        stopClip(trapTriggerSound);
    }

    private void stopClip(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void stopAllSounds() {
        stopBackgroundMusic();
        stopAllSfx();
    }
}
