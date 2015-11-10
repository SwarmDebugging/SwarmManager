package swarm.manager;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;


public class WorkbenchPreferencePage extends FieldEditorPreferencePage {

	public WorkbenchPreferencePage()
	{
		super(GRID);
	}

	@Override
	protected void createFieldEditors()
	{
		
		addField(new ComboFieldEditor("prefCombo", "A combo field", new String[][]{{"display1", "value1"},{"display2", "value2"}}, getFieldEditorParent()));

		addField(new ColorFieldEditor("prefColor", "Color for table items : ", getFieldEditorParent()));
		addField(new BooleanFieldEditor("prefBoolean", "A boolean : ", getFieldEditorParent()));
		addField(new StringFieldEditor("prefString", "A string : ", getFieldEditorParent()));
	}
}
