package com.azura4k.mcpe.Payroll.Commands;


import com.azura4k.mcpe.Payroll.Models.Business;
import com.azura4k.mcpe.Payroll.PayRollAPI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.geysermc.cumulus.CustomForm;


import java.util.ArrayList;
import java.util.List;

public class PayToCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        PayRollAPI api = new PayRollAPI();
        CustomForm form = new CustomForm(PayRollAPI.getLanguage("PayToFormTitle"));

        form.addElement("Topay", Input.builder().setName(PayRollAPI.getLanguage("PayToFormBusiness")).build());
        form.addElement("Amount", Input.builder().setName(PayRollAPI.getLanguage("PayToFormAmount")).build());
        form.addElement("Reason", Input.builder().setName(PayRollAPI.getLanguage("PayToFormReason")).build());
        form.addElement("Employee", Input.builder().setName(PayRollAPI.getLanguage("PayToFormEmployee")).build());
        form.addElement("Receipt", new Toggle(PayRollAPI.getLanguage("PayToFormAskforRecipt"), true));



        form.setHandler( (p, response) -> {
            String BusinessName = response.getInput("Topay").getValue();
            double Amount = Double.valueOf(response.getInput("Amount").getValue());
            String Employee = response.getInput("Employee").getValue();
            String Reason = response.getInput("Reason").getValue();
            boolean GiveReceipt = response.getToggle("Receipt").getValue();
            if (!BusinessName.isEmpty() && Amount > 0){

                Business business = api.LoadBusiness(BusinessName);
                if (business == null){
                    p.sendMessage(PayRollAPI.getLanguage("BusinessNotFound"));
                }
                else if (LlamaEconomy.getAPI().getMoney(p.getUniqueId()) >= Amount) {
                    business.Balance += Amount;
                    p.sendMessage(PayRollAPI.getLanguage("PayedToBusiness") + Amount + PayRollAPI.getLanguage("PayedToBusinessPt2") + BusinessName);
                    LlamaEconomy.getAPI().reduceMoney(p.getUniqueId(), Amount);
                    business.SaveData();
                    if (GiveReceipt) {
                        //COPIED FROM INFOBOOKS PLUGIN CODE BY PetteriM1
                        if (Reason == null){
                            Reason = PayRollAPI.getLanguage("ReasonNotProvided");
                        }
                        if (Employee == null){
                            Employee = PayRollAPI.getLanguage("EmployeeNameNotProvided");
                        }
                        String bookName = BusinessName + " " + PayRollAPI.getDate();
                        String bookAuthor = PayRollAPI.getLanguage("ReceiptAuthor");
                        String[] Contents = {PayRollAPI.getLanguage("ReceiptBusinessHeader") + BusinessName, PayRollAPI.getLanguage("ReceiptAmountHeader") + Amount, PayRollAPI.getLanguage("ReceiptEmployeeHeader") + Employee, PayRollAPI.getLanguage("ReceiptReasonHeader") + Reason};
                        ItemBookWritten book = (ItemBookWritten) Item.get(387, 0, 1);
                        book.writeBook(bookAuthor, bookName, Contents);
                        p.getInventory().addItem(book);
                    }
                }
                else if (LlamaEconomy.getAPI().getMoney(p.getUniqueId()) < Amount){
                    p.sendMessage(PayRollAPI.getLanguage("OverDraftRisk"));
                }

            }
            else {
                p.sendMessage(PayRollAPI.getLanguage("BusinessNotFound"));
            }

        });
        form.send(sender.getServer().getPlayerExact(sender.getName()));
        return false;
    }
}
