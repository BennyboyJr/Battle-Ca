package page;

import javax.swing.*;

public class KeybindPage extends Page {

    private static final long serialVersionUID = 1L;

    private final JBTN back = new JBTN(MainLocale.PAGE, "back");

    private final JL mmcam = new JL(MainLocale.PAGE, "movecam");
    private final JL mmprt = new JL(MainLocale.PAGE, "movepart");
    private final JL mmrot = new JL(MainLocale.PAGE, "rotpart");
    private final JL mmsca = new JL(MainLocale.PAGE, "scapart");

    protected KeybindPage(Page p) {
        super(p);

        ini();
    }


    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
    }

    @Override
    protected JButton getBackButton() {
        return back;
    }

    private void ini() {
        add(back);
        addListeners();
    }

    private void addListeners() {
        back.setLnr(this::getFront);
    }
}
