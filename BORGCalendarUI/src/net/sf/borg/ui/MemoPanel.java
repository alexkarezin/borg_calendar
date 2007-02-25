package net.sf.borg.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.borg.common.io.IOHelper;
import net.sf.borg.common.ui.StripedTable;
import net.sf.borg.common.ui.TableSorter;
import net.sf.borg.common.util.Errmsg;
import net.sf.borg.common.util.Resource;
import net.sf.borg.model.Memo;
import net.sf.borg.model.MemoModel;

public class MemoPanel extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = 1L;

    private JScrollPane jScrollPane = null;

    private StripedTable memoListTable = null;

    private JTextArea memoText = null;

    private JPanel buttonPanel = null;

    private JButton newButton = null;

    private JButton saveButton = null;

    private JButton delButton = null;

    private JScrollPane jScrollPane1 = null;

    private JSplitPane jSplitPane = null;

	private JButton exportButton = null;

    /**
         * This is the default constructor
         */
    public MemoPanel() {
	super();
	initialize();

	memoListTable.setModel(new TableSorter(new String[] { Resource
		.getResourceString("Memo_Name") },
		new Class[] { java.lang.String.class }));
	ListSelectionModel rowSM = memoListTable.getSelectionModel();
	rowSM.addListSelectionListener(this);
	memoText.setEditable(false);
	refresh();
    }

    /**
         * This method initializes this
         * 
         * @return void
         */
    private void initialize() {
	GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
	gridBagConstraints21.fill = GridBagConstraints.BOTH;
	gridBagConstraints21.weighty = 1.0;
	gridBagConstraints21.gridx = 0;
	gridBagConstraints21.gridy = 0;
	gridBagConstraints21.weightx = 1.0;
	GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
	gridBagConstraints2.gridx = 0;
	gridBagConstraints2.gridwidth = 1;
	gridBagConstraints2.fill = GridBagConstraints.BOTH;
	gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
	gridBagConstraints2.gridy = 1;
	this.setLayout(new GridBagLayout());
	this.add(getButtonPanel(), gridBagConstraints2);
	this.add(getJSplitPane(), gridBagConstraints21);
    }

    /**
         * This method initializes jScrollPane
         * 
         * @return javax.swing.JScrollPane
         */
    private JScrollPane getJScrollPane() {
	if (jScrollPane == null) {
	    jScrollPane = new JScrollPane();
	    jScrollPane
		    .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    jScrollPane.setPreferredSize(new Dimension(100, 423));
	    jScrollPane.setViewportView(getMemoListTable());
	}
	return jScrollPane;
    }

    /**
         * This method initializes memoListTable
         * 
         * @return javax.swing.JTable
         */
    private JTable getMemoListTable() {
	if (memoListTable == null) {
	    memoListTable = new StripedTable();
	    memoListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    memoListTable.setShowGrid(true);
	}
	return memoListTable;
    }

    /**
         * This method initializes memoText
         * 
         * @return javax.swing.JTextField
         */
    private JTextArea getMemoText() {
	if (memoText == null) {
	    memoText = new JTextArea();
	    memoText.setLineWrap(true);

	    memoText.setWrapStyleWord(true);
	}
	return memoText;
    }

    /**
         * This method initializes buttonPanel
         * 
         * @return javax.swing.JPanel
         */
    private JPanel getButtonPanel() {
	if (buttonPanel == null) {
	    buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout());
	    buttonPanel.add(getNewButton(), null);
	    buttonPanel.add(getSaveButton(), null);
	    buttonPanel.add(getDelButton(), null);
	    buttonPanel.add(getExportButton(), null);
	    
	}
	return buttonPanel;
    }

    /**
         * This method initializes newButton
         * 
         * @return javax.swing.JButton
         */
    private JButton getNewButton() {
	if (newButton == null) {
	    newButton = new JButton();
	    newButton.setText(Resource.getPlainResourceString("New_Memo"));
	    newButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    newMemo();
		}
	    });
	}
	return newButton;
    }

    /**
         * This method initializes saveButton
         * 
         * @return javax.swing.JButton
         */
    private JButton getSaveButton() {
	if (saveButton == null) {
	    saveButton = new JButton();
	    saveButton.setText(Resource.getPlainResourceString("Save_Memo"));
	    saveButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveMemo();
		}
	    });
	}
	return saveButton;
    }

    public void refresh() {

	try {
	    loadTable();
	} catch (Exception e) {
	    Errmsg.errmsg(e);
	}

    }

    private void loadTable() throws Exception {
	memoListTable.clearSelection();
	TableSorter tm = (TableSorter) memoListTable.getModel();
	tm.setRowCount(0);
	Collection names = MemoModel.getReference().getNames();
	// System.out.println("Names.size=" + names.size());
	Iterator it = names.iterator();
	while (it.hasNext()) {
	    tm.addRow(new Object[] { (String) it.next() });
	}
    }

    public void valueChanged(ListSelectionEvent e) {
	// Ignore extra messages.
	if (e.getValueIsAdjusting())
	    return;

	String memoName = getSelectedMemoName();
	if (memoName == null) {
	    memoText.setText("");
	    memoText.setEditable(false);
	    return;
	}

	String text;
	try {
	    text = MemoModel.getReference().getMemo(memoName).getMemoText();
	} catch (Exception e1) {
	    Errmsg.errmsg(e1);
	    return;
	}
	memoText.setEditable(true);
	memoText.setText(text);

    }


    private String getSelectedMemoName() {
	int row = memoListTable.getSelectedRow();
	if (row == -1) {
	    return null;
	}

	TableSorter tm = (TableSorter) memoListTable.getModel();
	String memoName = (String) tm.getValueAt(row, 0);
	return memoName;
    }

    private void saveMemo() {
	String name = getSelectedMemoName();
	if (name == null) {
	    Errmsg.notice(Resource
		    .getPlainResourceString("Select_Memo_Warning"));
	    return;
	}
	try {
	    Memo m = MemoModel.getReference().getMemo(name);
	    m.setMemoText(memoText.getText());
	    MemoModel.getReference().saveMemo(m);
	} catch (Exception e) {
	    Errmsg.errmsg(e);
	}

	refresh();
    }

    private void newMemo() {
	String name = JOptionPane.showInputDialog(Resource
		.getPlainResourceString("Enter_Memo_Name"));
	if (name == null)
	    return;

	try {
	    Memo existing = MemoModel.getReference().getMemo(name);
	    if (existing != null) {
		Errmsg.notice(Resource.getPlainResourceString("Existing_Memo"));
		return;
	    }
	} catch (Exception e1) {
	    Errmsg.errmsg(e1);
	}

	Memo m = new Memo();
	m.setMemoName(name);
	try {
	    MemoModel.getReference().saveMemo(m);
	} catch (Exception e) {
	    Errmsg.errmsg(e);
	}
	refresh();

    }

    private void deleteMemo()
    {
	String name = getSelectedMemoName();
	if (name == null) {
	    Errmsg.notice(Resource
		    .getPlainResourceString("Select_Memo_Warning"));
	    return;
	}
	try {
	    MemoModel.getReference().delete(name, true);
	} catch (Exception e) {
	    Errmsg.errmsg(e);
	}

	refresh();
    }
    
    /**
     * This method initializes delButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDelButton() {
        if (delButton == null) {
    	delButton = new JButton();
    	delButton.setText(Resource.getPlainResourceString("Delete_Memo"));
    	delButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    deleteMemo();
		}
	    });
        }
        return delButton;
    }

    /**
     * This method initializes jScrollPane1	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
    	jScrollPane1 = new JScrollPane();
    	jScrollPane1.setPreferredSize(new Dimension(400, 400));
    	jScrollPane1.setViewportView(getMemoText());
        }
        return jScrollPane1;
    }

    /**
     * This method initializes jSplitPane	
     * 	
     * @return javax.swing.JSplitPane	
     */
    private JSplitPane getJSplitPane() {
        if (jSplitPane == null) {
    	jSplitPane = new JSplitPane();
    	jSplitPane.setResizeWeight(0.2D);
    	jSplitPane.setLeftComponent(getJScrollPane());
    	jSplitPane.setRightComponent(getJScrollPane1());
        }
        return jSplitPane;
    }

	/**
	 * This method initializes exportButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExportButton() {
		if (exportButton == null) {
			exportButton = new JButton();
			exportButton.setText(Resource.getPlainResourceString("export"));
			exportButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 StringBuffer sb = new StringBuffer();
					 String s = memoText.getText();
					 for( int i = 0; i < s.length(); i++)
					 {
						 if( s.charAt(i) == '\n')
						 {
							 sb.append('\r');
						 }
						 sb.append(s.charAt(i));
						 
					 }
					 byte[] buf2 = sb.toString().getBytes();
					 ByteArrayInputStream istr = new ByteArrayInputStream(buf2);
					try {
						IOHelper.fileSave(".", istr, "");
					} catch (Exception e1) {
						Errmsg.errmsg(e1);
					}
				}
			});
		}
		return exportButton;
	}

	
}
