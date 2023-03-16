package ui;

import java.util.HashMap;

public interface SelectorListener {
	public void valuesSelected(Object selector, HashMap< String, Object > selectedValues);
	public void selectorClosed();
}
