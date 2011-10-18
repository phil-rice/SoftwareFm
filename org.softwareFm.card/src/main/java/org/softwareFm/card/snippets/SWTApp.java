package org.softwareFm.card.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SWTApp {

    Shell shell;

    public SWTApp(Display display) {

        shell = new Shell(display);

        shell.setText("Combo");

        initUI();

        shell.setSize(300, 250);
        shell.setLocation(300, 300);

        shell.open();

        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
    }


    public void initUI() {

        final Label label = new Label(shell, SWT.LEFT);
        label.setText("...");

        label.setLocation(50, 100);
        label.pack();

        final Combo combo = new Combo(shell, SWT.DROP_DOWN);
        combo.add("Ubuntu");
        combo.add("Fedora");
        combo.add("Mandriva");
        combo.add("Red Hat");
        combo.add("Mint");

        combo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                label.setText(combo.getText());
                label.pack();
            };
        });


        combo.setLocation(50, 30);
        combo.pack();
        combo.setSize(18,combo.getSize().y);

    }


    public static void main(String[] args) {
        Display display = new Display();
        new SWTApp(display);
        display.dispose();
    }
}