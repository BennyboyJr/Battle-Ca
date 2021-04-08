package page.info.filter;

import common.battle.data.CustomUnit;
import common.battle.data.MaskUnit;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.Data;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.unit.CustomTrait;
import common.util.unit.Form;
import common.util.unit.Unit;
import main.MainBCU;
import page.JTG;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static utilpc.Interpret.*;

public abstract class UnitFilterBox extends Page {

	private static final long serialVersionUID = 1L;

	public static UnitFilterBox getNew(Page p) {
		if (MainBCU.FILTER_TYPE == 0)
			return new UFBButton(p);
		if (MainBCU.FILTER_TYPE == 1)
			return new UFBList(p);
		return null;
	}

	public static UnitFilterBox getNew(Page p, String pack, String... parent) {
		if(MainBCU.FILTER_TYPE == 0) {
			return new UFBButton(p, pack, parent);
		} else if(MainBCU.FILTER_TYPE == 1) {
			return new UFBList(p, pack, parent);
		}

		return null;
	}

	public String name = "";
	protected final List<String> parents;
	protected final String pack;

	protected UnitFilterBox(Page p) {
		super(p);

		pack = null;
		parents = null;
	}

	protected UnitFilterBox(Page p, String pack, String... parent) {
		super(p);

		this.pack = pack;
		this.parents = Arrays.asList(parent);
	}

	public abstract int[] getSizer();

}

class UFBButton extends UnitFilterBox {

	private static final long serialVersionUID = 1L;

	private final JTG[] orop = new JTG[3];
	private final JTG[] rare = new JTG[RARITY.length];
	private final JTG[] trait = new JTG[9];
	private final JTG[] abis = new JTG[SABIS.length];
	private final JTG[] proc = new JTG[Data.PROC_TOT];
	private final JTG[] atkt = new JTG[ATKCONF.length];

	protected UFBButton(Page p) {
		super(p);

		ini();
		confirm();
	}

	protected UFBButton(Page p, String pack, String... parent) {
		super(p, pack, parent);

		ini();
		confirm();
	}

	@Override
	public void callBack(Object o) {
		confirm();
	}

	@Override
	public int[] getSizer() {
		return new int[] { 2000, 400, 1, 400 };
	}

	@Override
	protected void resized(int x, int y) {
		JTG[][] btns = new JTG[][] { rare, trait, abis, proc, atkt };
		AttList.btnDealer(x, y, btns, orop, -1, 0, 1, -1, 2);
	}

	private void confirm() {
		List<Form> ans = new ArrayList<>();
		for(PackData p : UserProfile.getAllPacks()) {
			for (Unit u : p.units.getList())
				for (Form f : u.forms) {
					MaskUnit du = f.maxu();
					int t = du.getType();
					int a = du.getAbi();
					boolean b0 = rare[u.rarity].isSelected();
					boolean b1 = !orop[0].isSelected();
					for (int i = 0; i < trait.length; i++)
						if (trait[i].isSelected())
							if (orop[0].isSelected())
								b1 |= ((t >> i) & 1) == 1;
							else
								b1 &= ((t >> i) & 1) == 1;
					boolean b2 = !orop[1].isSelected();
					for (int i = 0; i < abis.length; i++)
						if (abis[i].isSelected()) {
							boolean bind = ((a >> i) & 1) == 1;
							if (orop[1].isSelected())
								b2 |= bind;
							else
								b2 &= bind;
						}
					for (int i = 0; i < proc.length; i++)
						if (proc[i].isSelected())
							if (orop[1].isSelected())
								b2 |= du.getAllProc().getArr(i).exists();
							else
								b2 &= du.getAllProc().getArr(i).exists();
					boolean b3 = !orop[2].isSelected();
					for (int i = 0; i < atkt.length; i++)
						if (atkt[i].isSelected())
							if (orop[2].isSelected())
								b3 |= isType(du, i);
							else
								b3 &= isType(du, i);
					boolean b4 = true;

					String fname = MultiLangCont.getStatic().FNAME.getCont(f);

					if (fname == null)
						fname = f.name;

					if (fname == null)
						fname = "";

					if (name != null)
						b4 = fname.toLowerCase().contains(name.toLowerCase());

					boolean b5;

					if(pack == null)
						b5 = true;
					else {
						b5 = u.id.pack.equals(Identifier.DEF) || u.id.pack.equals(pack) || parents.contains(u.id.pack);
					}

					b0 = nonSele(rare) | b0;
					b1 = nonSele(trait) | b1;
					b2 = nonSele(abis) & nonSele(proc) | b2;
					b3 = nonSele(atkt) | b3;
					if (b0 & b1 & b2 & b3 & b4 & b5)
						ans.add(f);
				}
		}
		getFront().callBack(ans);
	}

	private void ini() {
		for (int i = 0; i < orop.length; i++)
			set(orop[i] = new JTG(0, "orop"));
		for (int i = 0; i < rare.length; i++)
			set(rare[i] = new JTG(RARITY[i]));
		for (int i = 0; i < trait.length; i++) {
			set(trait[i] = new JTG(TRAIT[i]));
			BufferedImage v = UtilPC.getIcon(3, i);
			if (v == null)
				continue;
			trait[i].setIcon(new ImageIcon(v));
		}
		for (int i = 0; i < abis.length; i++) {
			set(abis[i] = new JTG(SABIS[i]));
			BufferedImage v = UtilPC.getIcon(0, i);
			if (v == null)
				continue;
			abis[i].setIcon(new ImageIcon(v));
		}
		for (int i = 0; i < proc.length; i++) {
			set(proc[i] = new JTG(ProcLang.get().get(i).abbr_name));
			BufferedImage v = UtilPC.getIcon(1, i);
			if (v == null)
				continue;
			proc[i].setIcon(new ImageIcon(v));
		}
		for (int i = 0; i < atkt.length; i++) {
			set(atkt[i] = new JTG(ATKCONF[i]));
			BufferedImage v = UtilPC.getIcon(2, i);
			if (v == null)
				continue;
			atkt[i].setIcon(new ImageIcon(v));
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
		b.addActionListener(arg0 -> confirm());
	}

}

class UFBList extends UnitFilterBox {

	private static final long serialVersionUID = 1L;

	private final JTG[] orop = new JTG[3];
	private final JList<String> rare = new JList<>(RARITY);
	private final Vector<String> vt = new Vector<>();
	private final Vector<String> va = new Vector<>();
	private final AttList trait = new AttList(3, 0);
	private final AttList abis = new AttList(0, 0);
	private final AttList atkt = new AttList(2, 0);
	private final JScrollPane jr = new JScrollPane(rare);
	private final JScrollPane jt = new JScrollPane(trait);
	private final JScrollPane jab = new JScrollPane(abis);
	private final JScrollPane jat = new JScrollPane(atkt);

	protected UFBList(Page p) {
		super(p);

		ini();
		confirm();
	}

	protected UFBList(Page p, String pack, String... parent) {
		super(p, pack, parent);

		ini();
		confirm();
	}

	@Override
	public void callBack(Object o) {
		confirm();
	}

	@Override
	public int[] getSizer() {
		return new int[] { 450, 1150, 0, 500 };
	}

	@Override
	protected void resized(int x, int y) {
		set(orop[0], x, y, 0, 350, 200, 50);
		set(orop[1], x, y, 250, 0, 200, 50);
		set(orop[2], x, y, 0, 800, 200, 50);

		set(jr, x, y, 0, 50, 200, 250);
		set(jt, x, y, 0, 400, 200, 350);
		set(jab, x, y, 250, 50, 200, 1100);
		set(jat, x, y, 0, 850, 200, 300);
	}

	private final List<CustomTrait> trlis = new ArrayList<>();

	private void confirm() {
		List<Form> ans = new ArrayList<>();
		for(PackData p : UserProfile.getAllPacks()) {
			for (Unit u : p.units.getList())
				for (Form f : u.forms) {
					MaskUnit du = f.maxu();
					int t = du.getType();
					int a = du.getAbi();
					List<Identifier<CustomTrait>> ct = f.du instanceof CustomUnit ? ((CustomUnit)f.du).customTraits : null;
					boolean b0 = rare.isSelectedIndex(u.rarity);
					boolean b1 = !orop[0].isSelected(), selbc = false, seldiy = false;;
					for (int i : trait.getSelectedIndices())
						if (i < 9) {
							selbc = true;
							if (orop[0].isSelected())
								b1 |= ((t >> i) & 1) == 1;
							else
								b1 &= ((t >> i) & 1) == 1;
						} else if (ct != null && ct.size() > 0) {
							seldiy = true;
							if (orop[0].isSelected())
								for (Identifier<CustomTrait> diyt : ct)
									b1 |= trlis.get(i - 9).id.equals(diyt);
							else
								b1 &= ct.contains(trlis.get(i - 9).id);
						} else if (selbc && !orop[0].isSelected()) b1 = false;
						else if (!seldiy && !orop[0].isSelected()) b1 = false;
					boolean b2 = !orop[1].isSelected();
					int len = SABIS.length;
					for (int i : abis.getSelectedIndices())
						if (i < len) {
							boolean bind = ((a >> i) & 1) == 1;
							if (orop[1].isSelected())
								b2 |= bind;
							else
								b2 &= bind;
						} else if (orop[1].isSelected())
							b2 |= du.getAllProc().getArr(i - len).exists();
						else
							b2 &= du.getAllProc().getArr(i - len).exists();
					boolean b3 = !orop[2].isSelected();
					for (int i : atkt.getSelectedIndices())
						if (orop[2].isSelected())
							b3 |= isType(du, i);
						else
							b3 &= isType(du, i);

					boolean b4 = true;

					String fname = MultiLangCont.getStatic().FNAME.getCont(f);

					if (fname == null)
						fname = f.name;

					if (fname == null)
						fname = "";

					if (name != null)
						b4 = fname.toLowerCase().contains(name.toLowerCase());

					boolean b5;

					if(pack == null)
						b5 = true;
					else {
						b5 = u.id.pack.equals(Identifier.DEF) || u.id.pack.equals(pack) || parents.contains(u.id.pack);
					}

					b0 = rare.getSelectedIndex() == -1 | b0;
					b1 = trait.getSelectedIndex() == -1 | b1;
					b2 = abis.getSelectedIndex() == -1 | b2;
					b3 = atkt.getSelectedIndex() == -1 | b3;
					if (b0 & b1 & b2 & b3 & b4 & b5)
						ans.add(f);
				}

		}
		getFront().callBack(ans);
	}

	private void ini() {
		for (int i = 0; i < orop.length; i++)
			set(orop[i] = new JTG(get(0, "orop")));
		vt.addAll(Arrays.asList(TRAIT).subList(0, 9));
		Collection<PackData.UserPack> pacs = UserProfile.getUserPacks();
		for (PackData.UserPack pacc : pacs)
			for (CustomTrait ctra : pacc.diyTrait)
				if (pack == null || ctra.id.pack.equals(pack) || parents.contains(ctra.id.pack)) {
					trlis.add(ctra);
					vt.add(ctra.name);
				}
		customTraitsIco(trait,trlis);
		Collections.addAll(va, SABIS);
		for (int i = 0; i < Data.PROC_TOT; i++)
			va.add(ProcLang.get().get(i).abbr_name);
		trait.setListData(vt);
		abis.setListData(va);
		atkt.setListData(ATKCONF);
		set(rare);
		set(trait);
		set(abis);
		set(atkt);
		add(jr);
		add(jt);
		add(jab);
		add(jat);
	}

	protected static void customTraitsIco(AttList trait, List<CustomTrait> diyTraits) {
		trait.diyTraitIcons(trait, diyTraits, false);
	}

	private void set(AbstractButton b) {
		add(b);
		b.addActionListener(arg0 -> confirm());
	}

	private void set(JList<?> jl) {
		int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		jl.setSelectionMode(m);

		jl.addListSelectionListener(arg0 -> confirm());
	}

}
