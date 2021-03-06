package com.azura4k.mcpe.Payroll.Gui.PayrollGui.Manage.Options.Employees;

import com.azura4k.mcpe.Payroll.Models.Business;
import com.azura4k.mcpe.Payroll.Models.Employee;
import com.azura4k.mcpe.Payroll.PayRollAPI;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Objects;

public class EmployeeInfoPage {

    public void initialize(Player player, Business business, Employee employee){

        CustomForm.Builder Form = CustomForm.builder();

        PayRollAPI api = new PayRollAPI();
        Employee Manager = api.LoadEmployee(business, player.getName());
        //Checks if Player is Owner

        Form.title(PayRollAPI.getLanguage("EmployeeProfileFormTitle"));

        if (Objects.equals(Manager.playerUUID, business.Owner.getUniqueId())){
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormName") + employee.PlayerName);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormStartDate") + employee.StartDate);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormTotalMinutesWorked") + employee.TotalWorkMinutes);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormMinutesWorked") + employee.MinutesWorkedPerPayPeriod);
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormEmpTitle"), employee.Title);
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormRank"),String.valueOf(employee.Rank));
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormMaxMinutes"), String.valueOf(employee.MaximumMinutes));
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormWage"),String.valueOf(employee.Wage));
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormIsFired") + employee.Fired);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormDateFired") + employee.FiredDate);
        }
        else if (Manager.Rank >= business.TrustedRank && !Objects.equals(Manager.playerUUID, business.Owner.getUniqueId())){
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormName") + employee.PlayerName);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormStartDate") + employee.StartDate);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormTotalMinutesWorked") + employee.TotalWorkMinutes);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormMinutesWorked") + employee.MinutesWorkedPerPayPeriod);
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormEmpTitle"), employee.Title);
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormRank"),String.valueOf(employee.Rank));
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormMaxMinutes"), String.valueOf(employee.MaximumMinutes));
            Form.input(PayRollAPI.getLanguage("EmployeeProfileFormWage"),String.valueOf(employee.Wage));
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormIsFired") + employee.Fired);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormDateFired") + employee.FiredDate);
        }
        else {
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormName") + employee.PlayerName);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormEmpTitle") + employee.Title);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormRank") + employee.Rank);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormStartDate") + employee.StartDate);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormTotalMinutesWorked") + employee.TotalWorkMinutes);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormIsFired") + employee.Fired);
            Form.label(PayRollAPI.getLanguage("EmployeeProfileFormDateFired") + employee.FiredDate);
        }
        Form.responseHandler((form, responseData) -> {
            if (!form.isClosed(responseData)) {
                try {
                    CustomFormResponse response = form.parseResponse(responseData);

                    if (!(response.getInput(4) == null) && !response.getInput(4).isEmpty()) {
                        employee.Title = response.getInput(4);
                    }
                    if (!(response.getInput(5) == null) && !response.getInput(5).isEmpty()) {
                        int Rank = Integer.parseInt(response.getInput(5));
                        if (!(Rank > business.MaxRank || Rank < business.MinRank)) {
                            employee.Rank = Integer.parseInt(response.getInput(5));
                        }
                    }
                    if (!(response.getInput(6) == null) && !response.getInput(6).isEmpty()) {
                        employee.MaximumMinutes = Double.parseDouble(response.getInput(6));
                    }
                    if (!(response.getInput(7) == null) && !response.getInput(7).isEmpty()) {
                        employee.Wage = Double.parseDouble(response.getInput(7));
                    }

                    employee.SaveData();



                    player.getPlayer().sendMessage(PayRollAPI.getLanguage("SuccessfullyUpdated"));

                    EmployeeSelection customForm = new EmployeeSelection();
                    customForm.Initialize(player, business);

                }catch (Exception ignored){
                    PayRollAPI.plugin.getLogger().warning(ignored.getMessage());
                }
            }
        });
        FloodgateApi.getInstance().sendForm(player.getUniqueId(), Form);
    }
}


