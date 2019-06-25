package page.info.edit;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.CommonStatic;
import common.util.stage.Limit;
import common.util.stage.Stage;
import page.JBTN;
import page.JTF;
import page.Page;

public class LimitEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final JTF star = new JTF();
	private final JTF stag = new JTF();
	private final JList<Limit> jll = new JList<>();
	private final JScrollPane jspl = new JScrollPane(jll);
	private final JBTN addl = new JBTN(0, "add");
	private final JBTN reml = new JBTN(0, "rem");

	private final Stage st;

	protected LimitEditPage(Page p, Stage stage) {
		super(p);
		st = stage;
		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jspl, x, y, 1300, 100, 400, 800);
		set(addl, x, y, 1300, 900, 200, 50);
		set(reml, x, y, 1500, 900, 200, 50);
		set(stag, x, y, 1300, 950, 200, 50);
		set(star, x, y, 1500, 950, 200, 50);
	}

	private void addListeners$0() {
		back.setLnr(e -> changePanel(getFront()));

		star.setLnr(e -> {
			if (isAdj())
				return;
			Limit l = jll.getSelectedValue();
			int n = CommonStatic.parseIntN(star.getText()) - 1;
			if (n < 0)
				n = -1;
			if (n > 3)
				n = 0;
			if (l != null)
				l.star = n;
			setLimit(l);
		});

		stag.setLnr(e -> {
			if (isAdj())
				return;
			Limit l = jll.getSelectedValue();
			int n = CommonStatic.parseIntN(stag.getText());
			if (n < 0)
				n = -1;
			if (n >= st.map.list.size())
				n = 0;
			if (l != null)
				l.sid = n;
			setLimit(l);
		});

		addl.setLnr(e -> {
			st.map.lim.add(new Limit());
			setListL();
		});

		reml.setLnr(e -> {
			st.map.lim.remove(jll.getSelectedValue());
			setListL();
		});

		jll.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (isAdj() || jll.getValueIsAdjusting())
					return;
				setLimit(jll.getSelectedValue());
			}

		});

	}

	private void ini() {
		add(back);
		add(jspl);
		add(addl);
		add(reml);
		add(star);
		add(stag);
		setListL();
		addListeners$0();
	}

	private void setLimit(Limit l) {
		reml.setEnabled(l != null);
		star.setEditable(l != null);
		stag.setEditable(l != null);
		star.setText(l == null ? "" : l.star == -1 ? "all stars" : ((l.star + 1) + " star"));
		stag.setText(l == null ? "" : l.sid == -1 ? "all stages" : l.sid + " - " + st.map.list.get(l.sid));
		// TODO
	}

	private void setListL() {
		Limit l = jll.getSelectedValue();
		change(st.map.lim.toArray(new Limit[0]), x -> jll.setListData(x));
		if (!st.map.lim.contains(l))
			l = null;
		setLimit(l);
	}

}
