package com.unip.petcareconnect.ui;

import android.app.DatePickerDialog;
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
import com.unip.petcareconnect.model.Veterinario;
import com.unip.petcareconnect.repository.PetCareRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Tela de Consultas e Agendamentos do Tutor.
 * Veterinarios e horarios livres vem da API; regras (RN-002, RN-005) sao do servidor.
 */
public class ConsultasActivity extends AppCompatActivity {

    private RecyclerView rvConsultas;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddConsulta;

    private ConsultaAdapter adapter;
    private PetCareRepository repository;
    private final List<Consulta> consultaList = new ArrayList<>();
    private final List<Pet> petList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);

        repository = new PetCareRepository(this);

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
        fabAddConsulta.setOnClickListener(v -> abrirAgendamento());

        carregarDados();
    }

    private void carregarDados() {
        progressBar.setVisibility(View.VISIBLE);
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
                Snackbar.make(rvConsultas, "Erro ao carregar pets: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDetalhesConsulta(Consulta consulta) {
        String msg = "Pet: " + consulta.getPetNome() + "\n" +
                "Veterinário: " + (consulta.getVeterinarioNome() != null ? consulta.getVeterinarioNome() : "A definir") + "\n" +
                "Data: " + consulta.getData() + " às " + consulta.getHorario() + "\n" +
                "Status: " + consulta.getStatus();

        if (consulta.getObservacoes() != null && !consulta.getObservacoes().isEmpty()) {
            msg += "\n\nObservações: " + consulta.getObservacoes();
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalhes da Consulta")
                .setMessage(msg)
                .setPositiveButton("Fechar", null)
                .show();
    }

    /** Busca os veterinarios na API e so entao abre o dialogo. */
    private void abrirAgendamento() {
        if (petList.isEmpty()) {
            Toast.makeText(this, "Cadastre um pet antes de agendar uma consulta!", Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        repository.listarVeterinarios(new PetCareRepository.RepositoryCallback<List<Veterinario>>() {
            @Override
            public void onSuccess(List<Veterinario> vets) {
                progressBar.setVisibility(View.GONE);
                if (vets == null || vets.isEmpty()) {
                    Toast.makeText(ConsultasActivity.this, "Nenhum veterinário disponível no momento.", Toast.LENGTH_LONG).show();
                    return;
                }
                mostrarDialogAgendamento(vets);
            }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ConsultasActivity.this, "Erro ao carregar veterinários: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogAgendamento(List<Veterinario> vets) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_consulta, null);
        Spinner spinnerPets = dialogView.findViewById(R.id.spDialogConsultaPet);
        Spinner spinnerVets = dialogView.findViewById(R.id.spDialogConsultaVet);
        EditText etData   = dialogView.findViewById(R.id.etDialogConsultaData);
        EditText etHora   = dialogView.findViewById(R.id.etDialogConsultaHora);
        EditText etMotivo = dialogView.findViewById(R.id.etDialogConsultaMotivo);
        EditText etObs    = dialogView.findViewById(R.id.etDialogConsultaObs);

        List<String> nomesPets = new ArrayList<>();
        for (Pet p : petList) nomesPets.add(p.getNome());
        spinnerPets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomesPets));

        List<String> nomesVets = new ArrayList<>();
        for (Veterinario v : vets) nomesVets.add(v.getNome());
        spinnerVets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomesVets));

        TextInputLayout tilData = dialogView.findViewById(R.id.tilDialogConsultaData);
        TextInputLayout tilHora = dialogView.findViewById(R.id.tilDialogConsultaHora);

        etData.addTextChangedListener(new DateMaskWatcher(etData));
        etData.setOnClickListener(v -> abrirDatePicker(etData));
        if (tilData != null) tilData.setEndIconOnClickListener(v -> abrirDatePicker(etData));

        // Hora: escolhida entre os horarios livres que a API devolve
        etHora.setFocusable(false);
        View.OnClickListener escolherHora = v ->
                escolherHorario(vets.get(spinnerVets.getSelectedItemPosition()), etData, etHora);
        etHora.setOnClickListener(escolherHora);
        if (tilHora != null) tilHora.setEndIconOnClickListener(escolherHora);

        new AlertDialog.Builder(this)
                .setTitle("Novo Agendamento")
                .setView(dialogView)
                .setPositiveButton("Agendar", (dialog, which) -> {
                    Pet pet = petList.get(spinnerPets.getSelectedItemPosition());
                    Veterinario vet = vets.get(spinnerVets.getSelectedItemPosition());
                    String dataIso = paraIso(etData.getText().toString().trim());
                    String hora = etHora.getText().toString().trim();
                    String motivo = etMotivo.getText().toString().trim();
                    String obs = etObs.getText().toString().trim();

                    if (dataIso == null) {
                        Toast.makeText(this, "Preencha a data no formato dd/MM/yyyy!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(hora)) {
                        Toast.makeText(this, "Escolha um horário disponível!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(motivo)) {
                        Toast.makeText(this, "Preencha o motivo da consulta!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // O banco so tem "observacoes": o motivo entra no inicio do texto
                    String observacoes = "Motivo: " + motivo + (obs.isEmpty() ? "" : " | " + obs);

                    repository.agendarConsulta(pet.getId(), vet.getId(), dataIso, hora, observacoes,
                            new PetCareRepository.RepositoryCallback<String>() {
                                @Override
                                public void onSuccess(String mensagem) {
                                    Toast.makeText(ConsultasActivity.this, mensagem, Toast.LENGTH_SHORT).show();
                                    carregarDados();
                                }

                                @Override
                                public void onError(String mensagem) {
                                    Toast.makeText(ConsultasActivity.this,
                                            "Não foi possível agendar: " + mensagem, Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void escolherHorario(Veterinario vet, EditText etData, EditText etHora) {
        String dataIso = paraIso(etData.getText().toString().trim());
        if (dataIso == null) {
            Toast.makeText(this, "Escolha a data primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }
        repository.listarHorariosDisponiveis(vet.getId(), dataIso,
                new PetCareRepository.RepositoryCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> horarios) {
                        if (horarios == null || horarios.isEmpty()) {
                            Toast.makeText(ConsultasActivity.this,
                                    "Sem horários livres nesta data.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<String> opcoes = new ArrayList<>();
                        for (String h : horarios) {
                            opcoes.add(h != null && h.length() > 5 ? h.substring(0, 5) : h);
                        }
                        new AlertDialog.Builder(ConsultasActivity.this)
                                .setTitle("Horários livres")
                                .setItems(opcoes.toArray(new String[0]),
                                        (d, i) -> etHora.setText(opcoes.get(i)))
                                .show();
                    }

                    @Override
                    public void onError(String mensagem) {
                        Toast.makeText(ConsultasActivity.this,
                                "Erro ao buscar horários: " + mensagem, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmarCancelamento(Consulta consulta) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Consulta")
                .setMessage("Confirma o cancelamento da consulta de " + consulta.getPetNome() + "?\n\n"
                        + "As regras de cancelamento (RN-005) são aplicadas pelo servidor.")
                .setPositiveButton("Sim, cancelar", (dialog, which) ->
                        repository.cancelarConsulta(consulta.getId(), new PetCareRepository.RepositoryCallback<String>() {
                            @Override
                            public void onSuccess(String mensagem) {
                                Toast.makeText(ConsultasActivity.this, mensagem, Toast.LENGTH_LONG).show();
                                carregarDados();
                            }

                            @Override
                            public void onError(String mensagem) {
                                Toast.makeText(ConsultasActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                            }
                        }))
                .setNegativeButton("Não", null)
                .show();
    }

    private void abrirDatePicker(EditText campo) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this, (view, year, month, day) ->
                campo.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dlg.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dlg.show();
    }

    /** Valida dd/MM/yyyy e devolve no mesmo formato (a API exige dd/MM/yyyy); null se invalida. */
    private static String paraIso(String ddMMyyyy) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            f.setLenient(false);
            return f.parse(ddMMyyyy) == null ? null : ddMMyyyy;
        } catch (Exception e) {
            return null;
        }
    }

    // ---- Mascara de data ----

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

    /** Timestamp para ordenar a lista (aceita dd/MM/yyyy e yyyy-MM-dd). */
    private static long parseConsultaTimestamp(Consulta c) {
        if (c == null || c.getData() == null) return Long.MIN_VALUE;
        String dataStr = c.getData().trim();
        String horaStr = c.getHorario() != null ? c.getHorario().trim() : "00:00";
        if (horaStr.length() > 5) horaStr = horaStr.substring(0, 5);
        String dateTimeStr = dataStr + " " + horaStr;

        String[] formats = {"dd/MM/yyyy HH:mm", "dd/MM/yyyy", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};
        for (String fmt : formats) {
            for (String texto : new String[]{dateTimeStr, dataStr}) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.getDefault());
                    sdf.setLenient(false);
                    Date d = sdf.parse(texto);
                    if (d != null) return d.getTime();
                } catch (Exception ignored) {}
            }
        }
        return Long.MIN_VALUE;
    }
}