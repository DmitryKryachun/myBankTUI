package com.mybank.tui;

import com.mybank.data.DataSource;
import com.mybank.domain.*;
import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

import java.io.IOException;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;
    //private Bank bank = new Bank();
    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }


    private void ShowCustomerDetails() {

        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 25, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");
        DataSource dataSource = new DataSource("C:\\Users\\dimon\\Desktop\\SpringCourse-master\\2904\\src\\com\\mybank\\tui\\test.dat");
        try {
            dataSource.loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bank.addCustomer("Denis", "Petrov");
        Bank.getCustomer(Bank.getNumberOfCustomers()-1).addAccount(new CheckingAccount(200, 300));
        Bank.getCustomer(Bank.getNumberOfCustomers()-1).addAccount(new SavingsAccount(400, 300));
        Bank.addCustomer("Valeriy", "Albertovich");
        Bank.getCustomer(Bank.getNumberOfCustomers()-1).addAccount(new CheckingAccount(100, 300));

        custWin.addLabel("Enter customer number: ", 2, 5);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 30, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    Customer customer = Bank.getCustomer(custNum);
                    String ownerName = customer.getFirstName() + " " + Bank.getCustomer(custNum).getLastName();
                    String accounts = "";
                    for(int acct_idx = 0; acct_idx < customer.getNumberOfAccounts(); ++acct_idx) {
                        Account account = customer.getAccount(acct_idx);
                        String account_type = "Account type:                  ";
                        if (account instanceof SavingsAccount) {
                            account_type += " Savings Account";
                        } else if (account instanceof CheckingAccount) {
                            account_type += " Checking Account";
                        } else {
                            account_type += " Unknown Account Type";
                        }
                        accounts+= account_type+"\n"+account.getBalance()+"\n";
                    }
                    details.setText("Owner Name: "+ownerName+" (id="+custNum+")\n"+ accounts);
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }
}