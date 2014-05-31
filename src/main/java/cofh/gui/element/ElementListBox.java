package cofh.gui.element;

import cofh.gui.GuiBase;
import cofh.gui.GuiColor;
import cofh.gui.element.listbox.IListBoxElement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public abstract class ElementListBox extends ElementBase {

	public int borderColor = new GuiColor(120, 120, 120, 255).getColor();
	public int backgroundColor = new GuiColor(0, 0, 0, 255).getColor();
	public int selectedLineColor = new GuiColor(0, 0, 0, 255).getColor();
	public int textColor = new GuiColor(150, 150, 150, 255).getColor();
	public int selectedTextColor = new GuiColor(255, 255, 255, 255).getColor();

	private final int _marginTop = 2;
	private final int _marginLeft = 2;
	private final int _marginRight = 2;
	private final int _marginBottom = 2;

	private final List<IListBoxElement> _elements = new LinkedList<IListBoxElement>();

	private int _firstIndexDisplayed;
	private int _selectedIndex;

	public ElementListBox(GuiBase containerScreen, int x, int y, int width, int height) {

		super(containerScreen, x, y, width, height);
	}

	public void add(IListBoxElement element) {

		_elements.add(element);
	}

	public void add(Collection<? extends IListBoxElement> elements) {

		_elements.addAll(elements);
	}

	public void remove(IListBoxElement element) {

		_elements.remove(element);
	}

	public void removeAt(int index) {

		_elements.remove(index);
	}

	public int getContentWidth() {

		return sizeX - _marginLeft - _marginRight;
	}

	public int getContentHeight() {

		return sizeY - _marginTop - _marginBottom;
	}

	protected int getContentTop() {

		return posY + _marginTop;
	}

	protected int getContentLeft() {

		return posX + _marginLeft;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		drawModalRect(posX - 1, posY - 1, posX + sizeX + 1, posY + sizeY + 1, borderColor);
		drawModalRect(posX, posY, posX + sizeX, posY + sizeY, backgroundColor);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		int heightDrawn = 0;
		int nextElement = _firstIndexDisplayed;

		GL11.glDisable(GL11.GL_LIGHTING);

		while (nextElement < _elements.size() && heightDrawn + _elements.get(nextElement).getHeight() <= getContentHeight()) {
			if (nextElement == _selectedIndex) {
				_elements.get(nextElement).draw(this, getContentLeft(), getContentTop() + heightDrawn, selectedLineColor, selectedTextColor);
			} else {
				_elements.get(nextElement).draw(this, getContentLeft(), getContentTop() + heightDrawn, backgroundColor, textColor);
			}
			heightDrawn += _elements.get(nextElement).getHeight();
			nextElement++;
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		int heightChecked = 0;
		for (int i = _firstIndexDisplayed; i < _elements.size(); i++) {
			int elementHeight = _elements.get(i).getHeight();
			if (heightChecked + elementHeight > getContentHeight()) {
				break;
			}
			if (getContentTop() + heightChecked <= mouseY && getContentTop() + heightChecked + elementHeight >= mouseY) {
				setSelectedIndex(i);
				onElementClicked(_elements.get(i));
				break;
			}
			heightChecked += elementHeight;
		}
		return true;
	}

	@Override
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		if (movement > 0) {
			scrollUp();
		} else if (movement < 0) {
			scrollDown();
		}
		return true;
	}

	public void scrollDown() {

		int heightDisplayed = 0;
		int elementsDisplayed = 0;
		for (int i = _firstIndexDisplayed; i < _elements.size(); i++) {
			if (heightDisplayed + _elements.get(i).getHeight() > sizeY) {
				break;
			}
			heightDisplayed += _elements.get(i).getHeight();
			elementsDisplayed++;
		}

		if (_firstIndexDisplayed + elementsDisplayed < _elements.size()) {
			_firstIndexDisplayed++;
		}

		onScroll(_firstIndexDisplayed);
	}

	public void scrollUp() {

		if (_firstIndexDisplayed > 0) {
			_firstIndexDisplayed--;
		}
		onScroll(_firstIndexDisplayed);
	}

	public int getLastScrollPosition() {

		int position = _elements.size() - 1;
		int heightUsed = _elements.get(position).getHeight();

		while (position > 0 && heightUsed < sizeY) {
			position--;
			heightUsed += _elements.get(position).getHeight();
		}

		return position + 1;
	}

	public int getSelectedIndex() {

		return _selectedIndex;
	}

	public int getIndexOf(Object value) {

		for (int i = 0; i < _elements.size(); i++) {
			if (_elements.get(i).getValue().equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public IListBoxElement getSelectedElement() {

		return _elements.get(_selectedIndex);
	}

	public void setSelectedIndex(int index) {

		if (index >= 0 && index < _elements.size() && index != _selectedIndex) {
			_selectedIndex = index;
			onSelectionChanged(_selectedIndex, getSelectedElement());
		}
	}

	public IListBoxElement getElement(int index) {

		return _elements.get(index);
	}

	public int getElementCount() {

		return _elements.size();
	}

	public void scrollTo(int index) {

		if (index >= 0 && index < _elements.size()) {
			_firstIndexDisplayed = index;
		}
	}

	protected void onElementClicked(IListBoxElement element) {

	};

	protected void onScroll(int newStartIndex) {

	};

	protected abstract void onSelectionChanged(int newIndex, IListBoxElement newElement);

}
