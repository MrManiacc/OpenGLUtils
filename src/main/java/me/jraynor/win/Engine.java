package me.jraynor.win;

public interface Engine {
    void onStart();

    void onStop();


    void onTick(float tick);

    void onUpdate(float update);
}
