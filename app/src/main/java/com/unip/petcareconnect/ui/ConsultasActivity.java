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

/**
 * Tela de Consultas e Agendamentos do Tutor.
 * Permite listar, agendar e cancelar consultas.
 */
public class ConsultasActivity extends AppCompatActivity {

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

        repository     = new PetCareRepository(this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        rvConsultas     = findViewById(R.id.rvConsultas);
        swipeRefresh    = findViewById(R.id.swipeRefresh);
        progressBar     = findViewById(R.id.progressBar);
        tvEmptyState    = findViewById(R.id.tvEmptyState);
        fabAddConsulta  = findViewById(R.id.fabAddConsulta);

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

                repository.listarConsultas(new PetCareRepository.RepositoryCallback<List<Consulta>>() {
                    @Override
                    public void onSuccess(List<Consulta> consultas) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        consultaList.clear();
                        if (consultas != null && !consultas.isEmpty()) {
                            Collections.sort(consultas,
                                    (c1, c2) -> Long.compare(parseConsultaTimestamp(c2), parseConsultaTimestamp(c1)));
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
        Spinner spinnerVets = dialogView.findViewById(R.id.spDialogConsultaVet);
        EditText etDataConsulta    = dialogView.findViewById(R.id.etDialogConsultaData);
        EditText etHorarioConsulta = dialogView.findViewById(R.id.etDialogConsultaHora);
        EditText etMotivo          = dialogView.findViewById(R.id.etDialogConsultaMotivo);
        EditText etObservacoes     = dialogView.findViewById(R.id.etDialogConsultaObs);

        // Popula spinner de pets
        List<String> nomesPets = new ArrayList<>();
        for (Pet p : petList) {
            nomesPets.add(p.getNome() + " (" + p.getEspecie() + ")");
        }
        spinnerPets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomesPets));

        // Veterinários disponíveis (lista estática para agendamento pelo tutor)
        List<String> veterinarios = new ArrayList<>();
        veterinarios.add("Dr(a). Ana Lima — Clínica Geral");
        veterinarios.add("Dr(a). Carlos Mendes — Dermatologia");
        veterinarios.add("Dr(a). Fernanda Costa — Ortopedia");
        veterinarios.add("Dr(a). Ricardo Alves — Cardiologia");
        veterinarios.add("Dr(a). Juliana Rocha — Nutrição Animal");
        spinnerVets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, veterinarios));

        TextInputLayout tilData = dialogView.findViewById(R.id.tilDialogConsultaData);
        TextInputLayout tilHora = dialogView.findViewById(R.id.tilDialogConsultaHora);

        etDataConsulta.addTextChangedListener(new DateMaskWatcher(etDataConsulta));
        etDataConsulta.setOnClickListener(v -> abrirDatePicker(etDataConsulta));
        if (tilData != null) tilData.setEndIconOnClickListener(v -> abrirDatePicker(etDataConsulta));

        etHorarioConsulta.addTextChangedListener(new TimeMaskWatcher(etHorarioConsulta));
        etHorarioConsulta.setOnClickListener(v -> abrirTimePicker(etHorarioConsulta));
        if (tilHora != null) tilHora.setEndIconOnClickListener(v -> abrirTimePicker(etHorarioConsulta));

        new AlertDialog.Builder(this)
                .setTitle("Novo Agendamento")
                .setView(dialogView)
                .setPositiveButton("Agendar", (dialog, which) -> {
                    Pet petSelecionado    = petList.get(spinnerPets.getSelectedItemPosition());
                    String vetSelecionado = veterinarios.get(spinnerVets.getSelectedItemPosition());
                    String data    = etDataConsulta.getText().toString().trim();
                    String horario = etHorarioConsulta.getText().toString().trim();
                    String motivo  = etMotivo.getText().toString().trim();
                    String obs     = etObservacoes.getText().toString().trim();

                    if (TextUtils.isEmpty(data) || data.length() < 10) {
                        Toast.makeText(this, "Preencha a data no formato dd/MM/yyyy!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(horario) || horario.length() < 5) {
                        Toast.makeText(this, "Preencha o horário no formato HH:mm!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(motivo)) {
                        Toast.makeText(this, "Preencha o motivo da consulta!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Consulta novaConsulta = new Consulta();
                    novaConsulta.setPetId(petSelecionado.getId());
                    novaConsulta.setPetNome(petSelecionado.getNome());
                    novaConsulta.setTutorId(sessionManager.getUserId());
                    novaConsulta.setVeterinarioNome(vetSelecionado);
                    novaConsulta.setData(data);
                    novaConsulta.setHorario(horario);
                    novaConsulta.setMotivo(motivo);
                    novaConsulta.setObservacoes(obs);
                    novaConsulta.setStatus(Consulta.STATUS_MARCADA);

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
                .setMessage("Atenção: Cancelamentos com menos de 24h de antecedência serão registrados como 'Não Compareceu'.\n\nConfirma o cancelamento da consulta de " + consulta.getPetNome() + "?")
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

    private void abrirDatePicker(EditText campo) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this, (view, year, month, day) -> {
            String dataStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
            campo.setText(dataStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dlg.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dlg.show();
    }

    private void abrirTimePicker(EditText campo) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            campo.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
    }

    // ---- Máscaras de input ----

    public static class DateMaskWatcher implements TextWatcher {
        private final EditText editText;
        private boolean isUpdating = false;

        public DateMaskWatcher(EditText editText) { this.editText = editText; }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;
            String clean = s.toString().replaceAll("[^\\d]", "");
            if (clean.length() > 8) clean = clean.substring(0, 8);

            StringBuilder sb = new StringBuilder();
            if (clean.length() <= 2) {
                sb.append(clean);
            } else if (clean.length() <= 4) {
                sb.append(clean, 0, 2).append("/").append(clean.substring(2));
            } else {
                sb.append(clean, 0, 2).append("/").append(clean, 2, 4).append("/").append(clean.substring(4));
            }

            isUpdating = true;
            editText.setText(sb.toString());
            editText.setSelection(sb.length());
            isUpdating = false;
        }
    }

    public static class TimeMaskWatcher implements TextWatcher {
        private final EditText editText;
        private boolean isUpdating = false;

        public TimeMaskWatcher(EditText editText) { this.editText = editText; }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;
            String clean = s.toString().replaceAll("[^\\d]", "");
            if (clean.length() > 4) clean = clean.substring(0, 4);

            StringBuilder sb = new StringBuilder();
            if (clean.length() <= 2) {
                sb.append(clean);
            } else {
                sb.append(clean, 0, 2).append(":").append(clean.substring(2));
            }

            isUpdating = true;
            editText.setText(sb.toString());
            editText.setSelection(sb.length());
            isUpdating = false;
        }
    }

    /**
     * Converte data/hora de uma consulta em timestamp para ordenação.
     * Suporta formatos brasileiro (dd/MM/yyyy) e ISO (yyyy-MM-dd).
     */
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
}
