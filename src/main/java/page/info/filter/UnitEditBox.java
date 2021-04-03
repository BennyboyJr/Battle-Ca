package page.info.filter;

import common.pack.PackData.UserPack;
import common.util.unit.CustomTrait;
import page.Page;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static utilpc.Interpret.SABIS;
import static utilpc.Interpret.TRAIT;

public class UnitEditBox extends Page {

	private static final long serialVersionUID = 1L;

	private final boolean editable;

	private final Vector<String> vt = new Vector<>();
	private final Vector<String> va = new Vector<>();
	private final AttList trait = new AttList(3, 0);
	private final AttList abis = new AttList(0, 1);
	private final JScrollPane jt = new JScrollPane(trait);
	private final JScrollPane jab = new JScrollPane(abis);

	private boolean changing = false;
	private final List<CustomTrait> diyTraits;

	public UnitEditBox(Page p, UserPack pack) {
		super(p);
		editable = pack.editable;
		diyTraits = pack.diyTrait.getList();
		ini();
	}

	public void setData(int[] vals) {
		changing = true;
		trait.clearSelection();
		abis.clearSelection();
		for (int i = 0; i < 9; i++)
			if (((vals[0] >> i) & 1) > 0)
				trait.addSelectionInterval(i, i);
		for (int i = 0; i < SABIS.length; i++) {
			if (((vals[1] >> i) & 1) > 0)
				abis.addSelectionInterval(i, i);
		}
		changing = false;
	}

	@Override
	protected void resized(int x, int y) {
		set(jt, x, y, 0, 0, 300, 500);
		set(jab, x, y, 300, 0, 300, 500);
	}

	private void confirm() {
		int[] ans = new int[3];
		for (int i = 0; i < 9; i++)
			if (trait.isSelectedIndex(i))
				ans[0] |= 1 << i;
		int lev = SABIS.length;
		for (int i = 0; i < lev; i++)
			if (abis.isSelectedIndex(i))
				ans[1] |= 1 << i;
		getFront().callBack(ans);
	}

	private void ini() {
		vt.addAll(Arrays.asList(TRAIT).subList(0, 9));
		Collections.addAll(va, SABIS);
		for (int k = 0; k < diyTraits.size(); k++)
			vt.add(diyTraits.get(k).name);
		trait.setListData(vt);
		abis.setListData(va);
		int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		trait.setSelectionMode(m);
		abis.setSelectionMode(m);
		set(trait);
		set(abis);
		add(jt);
		add(jab);
		trait.setEnabled(editable);
		abis.setEnabled(editable);
	}

	private void set(JList<?> jl) {

		jl.addListSelectionListener(arg0 -> {
			if (!changing && !jl.getValueIsAdjusting())
				confirm();
		});
	}

}
