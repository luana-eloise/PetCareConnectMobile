package com.unip.petcareconnect.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.adapter.ConsultaAdapter;
import com.unip.petcareconnect.model.Consulta;
import com.unip.petcareconnect.model.Pet;
import com.unip.petcareconnect.repository.PetCareRepository;
import com.unip.petcareconnect.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConsultasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvConsultas;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddConsulta;

    private ConsultaAdapter adapter;
    private PetCareRepository repository;
    private SessionManager sessionManager;
    private final List<Consulta> consultaList = new ArrayList<>();
    private final List<Pet> petList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);

        repository = new PetCareRepository(this);
        sessionManager = new SessionManager(this);

        toolbar = findViewById(R.id.toolbar);
        rvConsultas = findViewById(R.id.rvConsultas);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddConsulta = findViewById(R.id.fabAddConsulta);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Consultas e Agendamentos");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvConsultas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConsultaAdapter(consultaList, new ConsultaAdapter.OnConsultaActionListener() {
            @Override
            public void onConsultaClick(Consulta consulta) {
                mostrarDetalhesConsulta(consulta);
            }

            @Override
            public void onCancelar(Consulta consulta) {
                confirmarCancelamento(consulta);
            }
        });
        rvConsultas.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarDados);
        fabAddConsulta.setOnClickListener(v -> mostrarDialogAgendamento());

        carregarDados();
    }

    private void carregarDados() {
        progressBar.setVisibility(View.VISIBLE);
        // Carrega pets primeiro para ter opções no agendamento
        repository.listarPets(new PetCareRepository.RepositoryCallback<List<Pet>>() {
            @Override
            public void onSuccess(List<Pet> pets) {
                petList.clear();
                if (pets != null) petList.addAll(pets);

                // Carrega consultas
                repository.listarConsultas(new PetCareRepository.RepositoryCallback<List<Consulta>>() {
                    @Override
                    public void onSuccess(List<Consulta> consultas) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        consultaList.clear();
                        if (consultas != null && !consultas.isEmpty()) {
                            Collections.sort(consultas, (c1, c2) -> Long.compare(parseConsultaTimestamp(c2), parseConsultaTimestamp(c1)));
                            consultaList.addAll(consultas);
                            tvEmptyState.setVisibility(View.GONE);
                        } else {
                            tvEmptyState.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String mensagem) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        Snackbar.make(rvConsultas, "Erro ao carregar consultas: " + mensagem, Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void mostrarDetalhesConsulta(Consulta consulta) {
        String msg = "Pet: " + consulta.getPetNome() + "\n" +
                "Veterinário: " + (consulta.getVeterinarioNome() != null ? consulta.getVeterinarioNome() : "A definir") + "\n" +
                "Data: " + consulta.getData() + " às " + consulta.getHorario() + "\n" +
                "Status: " + consulta.getStatus() + "\n" +
                "Motivo: " + consulta.getMotivo();

        if (consulta.getObservacoes() != null && !consulta.getObservacoes().isEmpty()) {
            msg += "\n\nObservações: " + consulta.getObservacoes();
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalhes da Consulta")
                .setMessage(msg)
                .setPositiveButton("Fechar", null)
                .show();
    }

    private void mostrarDialogAgendamento() {
        if (petList.isEmpty()) {
            Toast.makeText(this, "Cadastre um pet antes de agendar uma consulta!", Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_consulta, null);
        Spinner spinnerPets = dialogView.findViewById(R.id.spDialogConsultaPet);
        EditText etDataConsulta = dialogView.findViewById(R.id.etDialogConsultaData);
        EditText etHorarioConsulta = dialogView.findViewById(R.id.etDialogConsultaHora);
        EditText etMotivo = dialogView.findViewById(R.id.etDialogConsultaMotivo);
        EditText etObservacoes = dialogView.findViewById(R.id.etDialogConsultaObs);

        List<String> nomesPets = new ArrayList<>();
        for (Pet p : petList) {
            nomesPets.add(p.getNome() + " (" + p.getEspecie() + ")");
        }
        ArrayAdapter<String> petAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomesPets);
        spinnerPets.setAdapter(petAdapter);

        TextInputLayout tilData = dialogView.findViewById(R.id.tilDialogConsultaData);
        TextInputLayout tilHora = dialogView.findViewById(R.id.tilDialogConsultaHora);

        // DatePicker dialog e máscara brasileira dd/MM/yyyy
        etDataConsulta.addTextChangedListener(new DateMaskWatcher(etDataConsulta));
        etDataConsulta.setOnClickListener(v -> abrirDatePicker(etDataConsulta));
        if (tilData != null) {
            tilData.setEndIconOnClickListener(v -> abrirDatePicker(etDataConsulta));
        }

        // TimePicker dialog e máscara HH:mm
        etHorarioConsulta.addTextChangedListener(new TimeMaskWatcher(etHorarioConsulta));
        etHorarioConsulta.setOnClickListener(v -> abrirTimePicker(etHorarioConsulta));
        if (tilHora != null) {
            tilHora.setEndIconOnClickListener(v -> abrirTimePicker(etHorarioConsulta));
        }

        new AlertDialog.Builder(this)
                .setTitle("Novo Agendamento")
                .setView(dialogView)
                .setPositiveButton("Agendar", (dialog, which) -> {
                    int selectedPetIndex = spinnerPets.getSelectedItemPosition();
                    Pet petSelecionado = petList.get(selectedPetIndex);
                    String data = etDataConsulta.getText().toString().trim();
                    String horario = etHorarioConsulta.getText().toString().trim();
                    String motivo = etMotivo.getText().toString().trim();
                    String obs = etObservacoes.getText().toString().trim();

                    if (TextUtils.isEmpty(data) || data.length() < 10) {
                        Toast.makeText(this, "Preencha a data no formato brasileiro dd/MM/yyyy (ex: 17/09/2026)!", Toast.LENGTH_SHORT).show();
                        etDataConsulta.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(horario) || horario.length() < 5) {
                        Toast.makeText(this, "Preencha o horário no formato HH:mm (ex: 14:00)!", Toast.LENGTH_SHORT).show();
                        etHorarioConsulta.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(motivo)) {
                        Toast.makeText(this, "Preencha o motivo da consulta!", Toast.LENGTH_SHORT).show();
                        etMotivo.requestFocus();
                        return;
                    }

                    Consulta novaConsulta = new Consulta();
                    novaConsulta.setPetId(petSelecionado.getId());
                    novaConsulta.setPetNome(petSelecionado.getNome());
                    novaConsulta.setTutorId(sessionManager.getUserId());
                    novaConsulta.setVeterinarioNome("Dr(a). Clínico Geral");
                    novaConsulta.setData(data);
                    novaConsulta.setHorario(horario);
                    novaConsulta.setMotivo(motivo);
                    novaConsulta.setObservacoes(obs);
                    novaConsulta.setStatus("Agendada");

                    repository.agendarConsulta(novaConsulta, new PetCareRepository.RepositoryCallback<Consulta>() {
                        @Override
                        public void onSuccess(Consulta data) {
                            Toast.makeText(ConsultasActivity.this, "Consulta agendada com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarDados();
                        }

                        @Override
                        public void onError(String mensagem) {
                            Toast.makeText(ConsultasActivity.this, "Não foi possível agendar: " + mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarCancelamento(Consulta consulta) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Consulta")
                .setMessage("Atenção: Cancelamentos com menos de 24 horas de antecedência serão registrados como 'Não Compareceu'. Confirma o cancelamento da consulta de " + consulta.getPetNome() + "?")
                .setPositiveButton("Sim, cancelar", (dialog, which) -> {
                    repository.cancelarConsulta(consulta.getId(), new PetCareRepository.RepositoryCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            Toast.makeText(ConsultasActivity.this, "Consulta cancelada.", Toast.LENGTH_SHORT).show();
                            carregarDados();
                        }

                        @Override
                        public void onError(String mensagem) {
                            Toast.makeText(ConsultasActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void abrirDatePicker(EditText etDataConsulta) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dataStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            etDataConsulta.setText(dataStr);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void abrirTimePicker(EditText etHorarioConsulta) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String horaStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            etHorarioConsulta.setText(horaStr);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    public static class DateMaskWatcher implements TextWatcher {
        private final EditText editText;
        private boolean isUpdating = false;

        public DateMaskWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;

            String clean = s.toString().replaceAll("[^\\d]", "");
            if (clean.length() > 8) clean = clean.substring(0, 8);

            StringBuilder formatted = new StringBuilder();
            if (clean.length() > 0) {
                if (clean.length() <= 2) {
                    formatted.append(clean);
                } else if (clean.length() <= 4) {
                    formatted.append(clean.substring(0, 2)).append("/").append(clean.substring(2));
                } else {
                    formatted.append(clean.substring(0, 2)).append("/")
                             .append(clean.substring(2, 4)).append("/")
                             .append(clean.substring(4));
                }
            }

            isUpdating = true;
            editText.setText(formatted.toString());
            editText.setSelection(formatted.length());
            isUpdating = false;
        }
    }

    public static class TimeMaskWatcher implements TextWatcher {
        private final EditText editText;
        private boolean isUpdating = false;

        public TimeMaskWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;

            String clean = s.toString().replaceAll("[^\\d]", "");
            if (clean.length() > 4) clean = clean.substring(0, 4);

            StringBuilder formatted = new StringBuilder();
            if (clean.length() > 0) {
                if (clean.length() <= 2) {
                    formatted.append(clean);
                } else {
                    formatted.append(clean.substring(0, 2)).append(":").append(clean.substring(2));
                }
            }

            isUpdating = true;
            editText.setText(formatted.toString());
            editText.setSelection(formatted.length());
            isUpdating = false;
        }
    }

    private static long parseConsultaTimestamp(Consulta c) {
        if (c == null || c.getData() == null) return Long.MIN_VALUE;
        String dataStr = c.getData().trim();
        String horaStr = c.getHora() != null ? c.getHora().trim() : "00:00";
        String dateTimeStr = dataStr + " " + horaStr;

        String[] formats = {"dd/MM/yyyy HH:mm", "dd/MM/yyyy", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};
        for (String fmt : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.getDefault());
                sdf.setLenient(false);
                Date d = sdf.parse(dateTimeStr);
                if (d != null) return d.getTime();
            } catch (Exception ignored) {}
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.getDefault());
                sdf.setLenient(false);
                Date d = sdf.parse(dataStr);
                if (d != null) return d.getTime();
            } catch (Exception ignored) {}
        }
        return Long.MIN_VALUE;
    }

    private static boolean isConsultaMarcada(Consulta c) {
        if (c == null || c.getStatus() == null) return false;
        String status = c.getStatus().trim();
        return "Marcada".equalsIgnoreCase(status)
                || "Agendada".equalsIgnoreCase(status)
                || "Confirmada".equalsIgnoreCase(status);
    }
}
