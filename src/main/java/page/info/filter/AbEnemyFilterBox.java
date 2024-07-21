package page.info.filter;

import common.pack.FixIndexList;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import common.util.unit.Trait;
import main.MainBCU;
import page.JTG;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.*;

import static utilpc.Interpret.*;

public abstract class AbEnemyFilterBox extends Page {

    private static final long serialVersionUID = 1L;

    public static AbEnemyFilterBox getNew(Page p) {
        if (MainBCU.FILTER_TYPE == 0)
            return new AEFBButton(p);
        if (MainBCU.FILTER_TYPE == 1)
            return new AEFBList(p);

        return null;
    }

    public static AbEnemyFilterBox getNew(Page p, String pack, String... parents) {
        if (MainBCU.FILTER_TYPE == 0) {
            return new AEFBButton(p, pack, parents);
        } else if(MainBCU.FILTER_TYPE == 1) {
            return new AEFBList(p, pack, parents);
        }

        return null;
    }

    protected String name = "";
    protected final List<String> parents;
    protected final String pack;
    protected final List<AbEnemy> enem = new ArrayList<>();

    protected AbEnemyFilterBox(Page p) {
        super(p);

        pack = null;
        parents = null;
    }

    protected AbEnemyFilterBox(Page p, String pack, String... parent) {
        super(p);

        this.pack = pack;
        parents = new ArrayList<>();
        parents.addAll(Arrays.asList(parent));
    }

    protected abstract int[] getSizer();

    protected abstract List<AbEnemy> filterType();

    protected List<AbEnemy> filterName() {
        int minDiff = MainBCU.searchTolerance;
        List<AbEnemy> enemf = new ArrayList<>();
        for (AbEnemy e : enem) {
            if (e instanceof Enemy) {
                Enemy en = (Enemy) e;
                String fname = MultiLangCont.getStatic().ENAME.getCont(en);
                if (fname == null)
                    fname = en.names.toString();
                int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
                minDiff = Math.min(minDiff, diff);
                if (diff == minDiff)
                    enemf.add(e);
            } else { // assume random enemy
                EneRand er = (EneRand) e;
                int diff = UtilPC.damerauLevenshteinDistance(er.name.toLowerCase(), name.toLowerCase());
                if (diff == Math.min(minDiff, diff))
                    enemf.add(e);
            }
        }
        return enemf;
    }

    protected List<AbEnemy> filterNameDynamic() {
        if (name.isEmpty())
            return enem;

        int nlen = name.length();
        int toll = MainBCU.dynamicTolerance[Math.min(MainBCU.dynamicTolerance.length - 1, nlen)];

        String lowName = name.toLowerCase();
        String headFname;

        List<AbEnemy> enemf = new ArrayList<>();
        for (AbEnemy e : enem) {
            String fname = e instanceof Enemy ? MultiLangCont.getStatic().ENAME.getCont((Enemy) e) : ((EneRand) e).name;
            if (fname == null)
                continue;
            fname = fname.toLowerCase();

            switch (nlen) {
                case 1:
                    if(fname.startsWith(lowName) || fname.contains(" " + lowName))
                        enemf.add(e);
                    break;
                case 2:
                case 3:
                    if(fname.startsWith(lowName) || fname.contains(" " + lowName) || fname.contains(lowName + " ") || fname.endsWith(lowName))
                        enemf.add(e);
                    break;
                default:
                    headFname = fname.substring(0, Math.min(fname.length(), nlen + toll));
                    if( (fname.charAt(0) == lowName.charAt(0) && (UtilPC.damerauLevenshteinDistance(headFname, lowName) <= 2 * toll)) || fname.contains(lowName))
                        enemf.add(e);
                    break;
            }
        }
        return enemf;
    }

    /**
     0 - filter both type and name
     1 - only filter by name
     */
    protected void confirm(int type) {
        getFront().callBack(type == 0 ? filterType()
                : type == 1 ? (MainBCU.useDynamic ? filterNameDynamic() : filterName())
                : null);
    }

}

class AEFBButton extends AbEnemyFilterBox {

    private static final long serialVersionUID = 1L;

    private final JTG[] orop = new JTG[3];
    private final JTG[] rare = new JTG[ERARE.length];
    private final JTG[] trait = new JTG[TRAIT.length];
    private final JTG[] abis = new JTG[EABIIND.length];
    private final JTG[] proc = new JTG[EPROCIND.length];
    private final JTG[] atkt = new JTG[ATKCONF.length];

    protected AEFBButton(Page p) {
        super(p);

        ini();
        confirm(0);
    }

    protected AEFBButton(Page p, String pack, String... parents) {
        super(p, pack, parents);

        ini();
        confirm(0);
    }

    @Override
    protected JButton getBackButton() {
        return null;
    }

    @Override
    public void callBack(Object o) {
        confirm((int) o);
    }

    @Override
    protected int[] getSizer() {
        return new int[] { 2000, 400, 1, 400 };
    }

    @Override
    protected List<AbEnemy> filterType() {
        for(PackData p : UserProfile.getAllPacks()) {
            for (Enemy e : p.enemies.getList()) {
                List<Trait> ct = e.de.getTraits();
                int a = e.de.getAbi();
                boolean b0 = false;
                for (int i = 0; i < rare.length; i++)
                    if (rare[i].isSelected())
                        b0 |= isER(e, i);
                boolean b1 = !orop[0].isSelected();
                for (int i = 0; i < trait.length; i++)
                    if (!ct.isEmpty()) {
                        if (orop[0].isSelected())
                            for (Trait diyt : ct) {
                                b1 |= trlis.get(i).equals(diyt);
                                if (b1)
                                    break;
                            }
                        else {
                            b1 &= ct.contains(trlis.get(i));
                            if (!b1)
                                break;
                        }
                    } else b1 = false;
                boolean b2 = !orop[1].isSelected();
                for (int i = 0; i < abis.length; i++)
                    if (abis[i].isSelected()) {
                        boolean bind = ((a >> EABIIND[i]) & 1) == 1;
                        if (orop[1].isSelected())
                            b2 |= bind;
                        else
                            b2 &= bind;
                    }
                for (int i = 0; i < proc.length; i++)
                    if (proc[i].isSelected())
                        if (orop[1].isSelected())
                            b2 |= e.de.getAllProc().getArr(EPROCIND[i]).exists();
                        else
                            b2 &= e.de.getAllProc().getArr(EPROCIND[i]).exists();
                boolean b3 = !orop[2].isSelected();
                for (int i = 0; i < atkt.length; i++)
                    if (atkt[i].isSelected())
                        if (orop[2].isSelected())
                            b3 |= isType(e.de, i);
                        else
                            b3 &= isType(e.de, i);

                boolean b4 = pack == null || e.id.pack.equals(Identifier.DEF) || e.id.pack.equals(pack) || parents.contains(e.id.pack);

                b0 = nonSele(rare) | b0;
                b1 = nonSele(trait) | b1;
                b2 = nonSele(abis) & nonSele(proc) | b2;
                b3 = nonSele(atkt) | b3;
                if (b0 & b1 & b2 & b3 & b4)
                    enem.add(e);
            }
        }

        return filterName();
    }

    @Override
    protected void resized(int x, int y) {
        JTG[][] btns = new JTG[][] { rare, trait, abis, proc, atkt };
        AttList.btnDealer(x, y, btns, orop, -1, 0, 1, -1, 2);
    }

    private final List<Trait> trlis = new ArrayList<>();

    private void ini() {
        for (int i = 0; i < orop.length; i++)
            set(orop[i] = new JTG(0, "orop"));
        for (int i = 0; i < rare.length; i++)
            set(rare[i] = new JTG(ERARE[i]));
        for (int i = 0; i < trait.length; i++) {
            set(trait[i] = new JTG(TRAIT[i]));
            trait[i].setIcon(UtilPC.createIcon(3, i));
        }
        FixIndexList.FixIndexMap<Trait> BCtraits = UserProfile.getBCData().traits;
        for (int i = 0 ; i < BCtraits.size() - 1 ; i++)
            trlis.add(BCtraits.get(i));
        for (int i = 0; i < abis.length; i++) {
            set(abis[i] = new JTG(EABI[i]));
            abis[i].setIcon(UtilPC.createIcon(0, EABIIND[i]));
        }
        ProcLang proclang = ProcLang.get();
        for (int i = 0; i < proc.length; i++) {
            set(proc[i] = new JTG(proclang.get(EPROCIND[i]).abbr_name));
            proc[i].setIcon(UtilPC.createIcon(1, EPROCIND[i]));
        }
        for (int i = 0; i < atkt.length; i++) {
            set(atkt[i] = new JTG(ATKCONF[i]));
            atkt[i].setIcon(UtilPC.createIcon(2, i));
        }
    }

    private boolean nonSele(JTG[] jtbs) {
        int n = 0;
        for (JTG jtb : jtbs)
            if (jtb.isSelected())
                n++;
        return n == 0;
    }

    private void set(AbstractButton b) {
        add(b);
        b.addActionListener(arg0 -> confirm(0));
    }

}

class AEFBList extends AbEnemyFilterBox {

    private static final long serialVersionUID = 1L;

    private final JTG[] orop = new JTG[4];
    private final JList<String> rare = new JList<>(ERARE);
    private final Vector<String> va = new Vector<>();
    private final TraitList trait = new TraitList(false);
    private final AttList abis = new AttList(-1, EABIIND.length);
    private final AttList atkt = new AttList(2, 0);
    private final JScrollPane jr = new JScrollPane(rare);
    private final JScrollPane jt = new JScrollPane(trait);
    private final JScrollPane jab = new JScrollPane(abis);
    private final JScrollPane jat = new JScrollPane(atkt);

    protected AEFBList(Page p) {
        super(p);

        ini();
        confirm(0);
    }

    protected AEFBList(Page p, String pack, String... parent) {
        super(p, pack, parent);

        ini();
        confirm(0);
    }

    @Override
    protected JButton getBackButton() {
        return null;
    }

    @Override
    public void callBack(Object o) {
        confirm((int) o);
    }

    @Override
    protected int[] getSizer() {
        return new int[] { 450, 1150, 0, 500 };
    }

    @Override
    protected List<AbEnemy> filterType() {
        enem.clear();
        for (PackData p : UserProfile.getAllPacks()) {
            for (EneRand er : p.randEnemies) {
                boolean editable = pack == null || er.id.pack.equals(Identifier.DEF) || er.id.pack.equals(pack) || parents.contains(er.id.pack);
                if (editable)
                    enem.add(er);
            }
            for (Enemy e : p.enemies.getList()) {
                int a = e.de.getAbi();
                List<Trait> ct = e.de.getTraits();
                boolean b0 = !orop[3].isSelected();
                for (int r : rare.getSelectedIndices()) {
                    if (orop[3].isSelected())
                        b0 |= isER(e, r);
                    else
                        b0 &= isER(e, r);
                }
                boolean b1 = !orop[0].isSelected();
                for (int i : trait.getSelectedIndices())
                    if (!ct.isEmpty()) {
                        if (orop[0].isSelected())
                            for (Trait diyt : ct) {
                                b1 |= trait.list.get(i).equals(diyt);
                                if (b1)
                                    break;
                            }
                        else {
                            b1 &= ct.contains(trait.list.get(i));
                            if (!b1)
                                break;
                        }
                    } else b1 = false;
                boolean b2 = !orop[1].isSelected();
                int len = EABIIND.length;
                for (int i : abis.getSelectedIndices())
                    if (i < len) {
                        boolean bind = ((a >> EABIIND[i]) & 1) == 1;
                        if (orop[1].isSelected())
                            b2 |= bind;
                        else
                            b2 &= bind;
                    } else if (orop[1].isSelected())
                        b2 |= e.de.getAllProc().getArr(EPROCIND[i - len]).exists();
                    else
                        b2 &= e.de.getAllProc().getArr(EPROCIND[i - len]).exists();
                boolean b3 = !orop[2].isSelected();
                for (int i : atkt.getSelectedIndices())
                    if (orop[2].isSelected())
                        b3 |= isType(e.de, i);
                    else
                        b3 &= isType(e.de, i);
                boolean b4 = pack == null || e.id.pack.equals(Identifier.DEF) || e.id.pack.equals(pack) || parents.contains(e.id.pack);

                b0 = rare.getSelectedIndex() == -1 | b0;
                b1 = trait.getSelectedIndex() == -1 | b1;
                b2 = abis.getSelectedIndex() == -1 | b2;
                b3 = atkt.getSelectedIndex() == -1 | b3;
                if (b0 && b1 && b2 && b3 && b4)
                    enem.add(e);
            }
        }

        return filterName();
    }

    @Override
    protected void resized(int x, int y) {
        set(orop[0], x, y, 0, 350, 200, 50);
        set(orop[1], x, y, 250, 0, 200, 50);
        set(orop[2], x, y, 0, 800, 200, 50);
        set(orop[3], x, y, 0, 0, 200, 50);

        set(jr, x, y, 0, 50, 200, 250);
        set(jt, x, y, 0, 400, 200, 350);
        set(jab, x, y, 250, 50, 200, 1100);
        set(jat, x, y, 0, 850, 200, 300);
    }

    private void ini() {
        for (int i = 0; i < orop.length; i++)
            set(orop[i] = new JTG(get(0, "orop")));
        FixIndexList.FixIndexMap<Trait> BCtraits = UserProfile.getBCData().traits;
        for (int i = 0 ; i < BCtraits.size() - 1 ; i++)
            trait.list.add(BCtraits.get(i));
        Collection<PackData.UserPack> pacs = UserProfile.getUserPacks();
        for (PackData.UserPack pacc : pacs)
            for (Trait ctra : pacc.traits)
                if (pack == null || ctra.id.pack.equals(pack) || parents.contains(ctra.id.pack))
                    trait.list.add(ctra);

        trait.setListData();
        va.addAll(Arrays.asList(EABI).subList(0, EABIIND.length));
        ProcLang proclang = ProcLang.get();
        for (int i = 0; i < EPROCIND.length; i++)
            va.add(proclang.get(EPROCIND[i]).abbr_name);
        abis.setListData(va);
        atkt.setListData(ATKCONF);
        int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        trait.setSelectionMode(m);
        abis.setSelectionMode(m);
        atkt.setSelectionMode(m);
        set(rare);
        set(trait);
        set(abis);
        set(atkt);
        add(jr);
        add(jt);
        add(jab);
        add(jat);
    }

    private void set(AbstractButton b) {
        add(b);
        b.addActionListener(arg0 -> confirm(0));
    }

    private void set(JList<?> jl) {

        jl.addListSelectionListener(arg0 -> confirm(0));
    }
}