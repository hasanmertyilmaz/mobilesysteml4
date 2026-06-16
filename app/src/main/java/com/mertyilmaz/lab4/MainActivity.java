package com.mertyilmaz.lab4;

/*
 * Name: Hasan Yilmaz
 * Student ID: 56505
 * Lab: 4
 * Course: Introduction to Mobile Systems
 */

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * UI of the rental — the Android equivalent of the console menu:
 * Park / Add / Remove / Refuel / Sort buttons plus the always-visible
 * vehicle list ("print all vehicles") and the garages overview.
 *
 * XML lifecycle: the database is loaded at program start (saved file if it
 * exists, otherwise the initial database from assets) and saved in onStop(),
 * i.e. before the program terminates.
 */
public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "vehicles.xml";

    /** Kept static so the state survives screen rotation within one process. */
    private static Rental rental;

    private TextView textStatus;
    private TextView textGarages;
    private ArrayAdapter<String> adapter;
    private final List<String> lines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = findViewById(R.id.textStatus);
        textGarages = findViewById(R.id.textGarages);
        ListView listVehicles = findViewById(R.id.listVehicles);
        adapter = new ArrayAdapter<>(this, R.layout.item_vehicle, lines);
        listVehicles.setAdapter(adapter);

        findViewById(R.id.btnPark).setOnClickListener(v -> showParkDialog());
        findViewById(R.id.btnAdd).setOnClickListener(v -> showAddDialog());
        findViewById(R.id.btnRemove).setOnClickListener(v -> showRemoveDialog());
        findViewById(R.id.btnRefuel).setOnClickListener(v -> showRefuelDialog());
        findViewById(R.id.btnSort).setOnClickListener(v -> {
            rental.sortVehicles();
            refresh(getString(R.string.msg_sorted));
        });

        String message = getString(R.string.msg_ready);
        if (rental == null) {
            rental = new Rental(5); // this assignment uses 5 garages
            message = loadVehicles();
        }
        refresh(message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveVehicles(); // save before program termination
    }

    /** Loads the saved database, or the initial one from assets on first run. */
    private String loadVehicles() {
        try {
            File saved = new File(getFilesDir(), FILE_NAME);
            InputStream in = saved.exists()
                    ? new FileInputStream(saved)
                    : getAssets().open(FILE_NAME);
            List<Vehicle> loaded = XmlStorage.load(in);
            in.close();
            for (Vehicle vehicle : loaded) {
                rental.addVehicle(vehicle);
            }
            return getString(R.string.msg_loaded, loaded.size(),
                    saved.exists() ? getString(R.string.src_saved) : getString(R.string.src_assets));
        } catch (Exception e) {
            return getString(R.string.msg_load_failed, e.getMessage());
        }
    }

    private void saveVehicles() {
        try (FileOutputStream out = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
            XmlStorage.save(out, rental.getVehicles());
        } catch (Exception e) {
            textStatus.setText(getString(R.string.msg_save_failed, e.getMessage()));
        }
    }

    /** Rebuilds the vehicle list (operation 4: print all vehicles) and garages line. */
    private void refresh(String message) {
        lines.clear();
        for (Vehicle vehicle : rental.getVehicles()) {
            lines.add(vehicle.toString());
        }
        adapter.notifyDataSetChanged();

        StringBuilder garages = new StringBuilder(getString(R.string.label_garages));
        for (Garage garage : rental.getGarages()) {
            garages.append("  ").append(garage.getNumber()).append(':')
                    .append(garage.isEmpty()
                            ? "-"
                            : "#" + ((Vehicle) garage.getParkedVehicle()).getId());
        }
        textGarages.setText(garages.toString());
        textStatus.setText(message);
    }

    // --- operation dialogs -------------------------------------------------

    /** Operation 1: park (positive button) / unpark (neutral button). */
    private void showParkDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_park, null);
        EditText editId = view.findViewById(R.id.editVehicleId);
        EditText editGarage = view.findViewById(R.id.editGarageNumber);

        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_park)
                .setView(view)
                .setPositiveButton(R.string.btn_park, (d, w) -> {
                    Integer id = parseInt(editId);
                    Integer garage = parseInt(editGarage);
                    if (id == null || garage == null) {
                        refresh(getString(R.string.msg_need_id_garage));
                    } else {
                        refresh(rental.parkVehicle(id, garage));
                    }
                })
                .setNeutralButton(R.string.btn_unpark, (d, w) -> {
                    Integer id = parseInt(editId);
                    refresh(id == null
                            ? getString(R.string.msg_need_id)
                            : rental.unparkVehicle(id));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Operation 2: add a new vehicle from keyboard input. */
    private void showAddDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add, null);
        Spinner spinnerType = view.findViewById(R.id.spinnerType);
        EditText editName = view.findViewById(R.id.editName);
        EditText editLiters = view.findViewById(R.id.editLiters);

        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_add)
                .setView(view)
                .setPositiveButton(R.string.btn_add, (d, w) -> {
                    String name = editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        refresh(getString(R.string.msg_need_name));
                        return;
                    }
                    int mask = readFuelMask(view);
                    Vehicle vehicle;
                    switch (spinnerType.getSelectedItemPosition()) {
                        case 0: vehicle = new Car(name, mask); break;
                        case 1: vehicle = new Motorboat(name, mask); break;
                        case 2: vehicle = new Bicycle(name); break;
                        default: vehicle = new Scooter(name); break;
                    }
                    Double liters = parseDouble(editLiters);
                    if (liters != null && liters > 0) {
                        if (vehicle instanceof Car) {
                            ((Car) vehicle).setFuelAmount(liters);
                        } else if (vehicle instanceof Motorboat) {
                            ((Motorboat) vehicle).setFuelAmount(liters);
                        }
                    }
                    rental.addVehicle(vehicle);
                    refresh(getString(R.string.msg_added, vehicle.toString()));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Operation 3: remove by ID (auto-unparks first, message says so). */
    private void showRemoveDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_remove, null);
        EditText editId = view.findViewById(R.id.editVehicleId);

        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_remove)
                .setView(view)
                .setPositiveButton(R.string.btn_remove, (d, w) -> {
                    Integer id = parseInt(editId);
                    refresh(id == null
                            ? getString(R.string.msg_need_id)
                            : rental.removeVehicle(id));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Extra operation demonstrating CombustionVehicle.refuel() rules. */
    private void showRefuelDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_refuel, null);
        EditText editId = view.findViewById(R.id.editVehicleId);
        EditText editLiters = view.findViewById(R.id.editLiters);

        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_refuel)
                .setView(view)
                .setPositiveButton(R.string.btn_refuel, (d, w) -> {
                    Integer id = parseInt(editId);
                    Double liters = parseDouble(editLiters);
                    if (id == null || liters == null) {
                        refresh(getString(R.string.msg_need_id_liters));
                        return;
                    }
                    Vehicle vehicle = rental.findById(id);
                    if (vehicle == null) {
                        refresh(getString(R.string.msg_no_vehicle, id));
                        return;
                    }
                    if (!(vehicle instanceof CombustionVehicle)) {
                        refresh(getString(R.string.msg_not_combustion, id));
                        return;
                    }
                    CombustionVehicle combustion = (CombustionVehicle) vehicle;
                    int mask = readFuelMask(view);
                    boolean ok = combustion.refuel(mask, liters);
                    refresh(ok
                            ? getString(R.string.msg_refueled, id, combustion.getFuelAmount())
                            : getString(R.string.msg_refuel_failed,
                                    CombustionVehicle.describeFuelMask(mask),
                                    CombustionVehicle.describeFuelMask(
                                            combustion.getSupportedFuelMask())));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // --- helpers ------------------------------------------------------------

    /** Reads the DIESEL/PETROL/LPG/CNG checkboxes into a fuel bitmask. */
    private int readFuelMask(View view) {
        int mask = 0;
        if (((CheckBox) view.findViewById(R.id.checkDiesel)).isChecked()) mask |= CombustionVehicle.DIESEL;
        if (((CheckBox) view.findViewById(R.id.checkPetrol)).isChecked()) mask |= CombustionVehicle.PETROL;
        if (((CheckBox) view.findViewById(R.id.checkLpg)).isChecked()) mask |= CombustionVehicle.LPG;
        if (((CheckBox) view.findViewById(R.id.checkCng)).isChecked()) mask |= CombustionVehicle.CNG;
        return mask;
    }

    private static Integer parseInt(EditText edit) {
        try {
            return Integer.parseInt(edit.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDouble(EditText edit) {
        try {
            return Double.parseDouble(edit.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
