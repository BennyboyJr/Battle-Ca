package page.info.edit;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.util.stage.Limit;
import common.util.stage.StageLimit;
import page.*;
import page.pack.CharaGroupPage;
import page.pack.LvRestrictPage;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LimitTable extends Page {

	private static final long serialVersionUID = 1L;

	private static String[] limits, rarity;

	static {
		redefine();
	}

	protected static void redefine() {
		limits = Page.get(MainLocale.INFO, "ht1", 7);
		rarity = Page.get(MainLocale.UTIL, "r", 6);
	}

	private final JTF min = new JTF();
	private final JTF num = new JTF();
	private final JTF max = new JTF();
	private final JTF jcg = new JTF();
	private final JTF jlr = new JTF();
	private final JBTN cgb = new JBTN(1, "ht15");
	private final JBTN lrb = new JBTN(1, "ht16");
	private final JTG one = new JTG(1, "ht12");
	private final JL rar = new JL(1, "ht10");
	private final JTG[] brars = new JTG[6];

	private final UserPack pac;

	private CharaGroupPage cgp;
	private LvRestrictPage lrp;

	private Limit lim;

	protected LimitTable(Page p0, UserPack p) {
		super(p0);
		pac = p;
		ini();
	}

	protected void abler(boolean b) {
		min.setEnabled(b);
		num.setEnabled(b);
		max.setEnabled(b);
		one.setEnabled(b);
		cgb.setEnabled(b);
		jcg.setEnabled(b);
		lrb.setEnabled(b);
		jlr.setEnabled(b);
		for (JTG jtb : brars)
			jtb.setEnabled(b);
	}

	@Override
	protected JButton getBackButton() {
		return null;
	}

	@Override
	protected void renew() {
		if (cgp != null) {
			jcg.setText("" + cgp.cg);
			lim.group = cgp.cg;
		}
		if (lrp != null) {
			jlr.setText("" + lrp.lr);
			lim.lvr = lrp.lr;
		}

		cgp = null;
		lrp = null;
	}

	@Override
	protected void resized(int x, int y) {
		int w = 1400 / 8;
		set(rar, x, y, 0, 0, w, 50);
		for (int i = 0; i < brars.length; i++)
			set(brars[i], x, y, w + w * i, 0, w, 50);
		set(min, x, y, 0, 50, w, 50);
		set(max, x, y, w, 50, w, 50);
		set(num, x, y, w * 2, 50, w, 50);
		set(one, x, y, w * 3, 50, w, 50);
		set(cgb, x, y, w * 4, 50, w, 50);
		set(jcg, x, y, w * 5, 50, w, 50);
		set(lrb, x, y, w * 6, 50, w, 50);
		set(jlr, x, y, w * 7, 50, w, 50);


	}

	protected void setLimit(Limit l) {
		lim = l;
		if (l == null) {
			for (int i = 0; i < brars.length; i++)
				brars[i].setSelected(false);
			max.setText(limits[4] + ": ");
			min.setText(limits[3] + ": ");
			num.setText(limits[1] + ": ");
			jcg.setText("");
			jlr.setText("");
			one.setSelected(false);
			abler(false);
			return;
		}
		abler(true);
		if (lim.rare > 0) {
			for (int i = 0; i < brars.length; i++)
				brars[i].setSelected(((lim.rare >> i) & 1) > 0);
		} else {
			for (int i = 0; i < brars.length; i++)
				brars[i].setSelected(true);
		}
		max.setText(limits[4] + ": " + lim.max);
		min.setText(limits[3] + ": " + lim.min);
		num.setText(limits[1] + ": " + lim.num);
		jcg.setText("" + lim.group);
		jlr.setText("" + lim.lvr);
		one.setSelected(lim.line == 1);
	}

	protected void setStageLimit(StageLimit sl) {

	}

	private void addListeners() {

		one.addActionListener(arg0 -> lim.line = one.isSelected() ? 1 : 0);

		for (int i = 0; i < brars.length; i++) {
			int I = i;
			brars[i].addActionListener(e -> {
				if (getFront().isAdj())
					return;
				lim.rare ^= 1 << I;
				getFront().callBack(lim);
			});
		}

		cgb.addActionListener(arg0 -> {
			cgp = new CharaGroupPage(getFront(), pac, false);
			changePanel(cgp);
		});

		lrb.addActionListener(arg0 -> {
			lrp = new LvRestrictPage(getFront(), pac, false);
			changePanel(lrp);
		});
	}

	private void ini() {
		set(rar);
		add(cgb);
		add(lrb);
		add(one);
		set(min);
		set(max);
		set(num);
		set(jcg);
		set(jlr);


		for (int i = 0; i < brars.length; i++) {
			add(brars[i] = new JTG(rarity[i]));
			brars[i].setSelected(true);
		}
		addListeners();
	}

	private void input(JTF jtf, String str) {
		int val = CommonStatic.parseIntN(str);
		if (jtf == max) {
			if (val < 0)
				return;
			lim.max = val;
		}
		if (jtf == min) {
			if (val < 0)
				return;
			lim.min = val;
		}
		if (jtf == num) {
			if (val < 0 || val > 50)
				return;
			lim.num = val;
		}
	}

	private void set(JLabel jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		jl.setBorder(BorderFactory.createEtchedBorder());
		add(jl);
	}

	private void set(JTF jtf) {
		add(jtf);

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (getFront().isAdj())
					return;
				input(jtf, jtf.getText());
				getFront().callBack(lim);
			}
		});

	}

}
