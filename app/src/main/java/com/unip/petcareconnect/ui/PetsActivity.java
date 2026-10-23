package com.unip.petcareconnect.ui;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.adapter.PetAdapter;
import com.unip.petcareconnect.model.Pet;
import com.unip.petcareconnect.repository.PetCareRepository;
import com.unip.petcareconnect.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela de gerenciamento de Pets do Tutor.
 * Permite listar, cadastrar, editar e excluir pets.
 */
public class PetsActivity extends AppCompatActivity {

    private RecyclerView rvPets;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private PetAdapter adapter;
    private PetCareRepository repository;
    private SessionManager sessionManager;
    private final List<Pet> petList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);

        repository     = new PetCareRepository(this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        rvPets       = findViewById(R.id.rvPets);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar  = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        FloatingActionButton fabAddPet = findViewById(R.id.fabAddPet);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meus Pets");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvPets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PetAdapter(petList, new PetAdapter.OnPetActionListener() {
            @Override
            public void onPetClick(Pet pet) { mostrarOpcoesPet(pet); }

            @Override
            public void onEdit(Pet pet) { mostrarDialogPet(pet); }

            @Override
            public void onDelete(Pet pet) { confirmarExclusaoPet(pet); }
        });
        rvPets.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarPets);
        fabAddPet.setOnClickListener(v -> mostrarDialogPet(null));

        carregarPets();
    }

    private void carregarPets() {
        progressBar.setVisibility(View.VISIBLE);
        repository.listarPets(new PetCareRepository.RepositoryCallback<List<Pet>>() {
            @Override
            public void onSuccess(List<Pet> pets) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                petList.clear();
                if (pets != null && !pets.isEmpty()) {
                    petList.addAll(pets);
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
                Snackbar.make(rvPets, "Erro ao carregar pets: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarOpcoesPet(Pet pet) {
        CharSequence[] opcoes = {"Editar dados", "Ver Histórico Clínico", "Excluir pet"};
        new AlertDialog.Builder(this)
                .setTitle(pet.getNome())
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogPet(pet);
                    } else if (which == 1) {
                        android.content.Intent intent = new android.content.Intent(PetsActivity.this, HistoricoActivity.class);
                        intent.putExtra(HistoricoActivity.EXTRA_PET_ID, pet.getId());
                        intent.putExtra(HistoricoActivity.EXTRA_PET_NOME, pet.getNome());
                        startActivity(intent);
                    } else {
                        confirmarExclusaoPet(pet);
                    }
                })
                .show();
    }

    private void mostrarDialogPet(Pet petExistente) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pet, null);
        EditText etNomePet      = dialogView.findViewById(R.id.etDialogPetNome);
        Spinner  spinnerEspecie = dialogView.findViewById(R.id.spDialogPetTipo);
        EditText etRaca         = dialogView.findViewById(R.id.etDialogPetRaca);
        EditText etIdade        = dialogView.findViewById(R.id.etDialogPetIdade);
        EditText etPeso         = dialogView.findViewById(R.id.etDialogPetPeso);

        String[] especies = {"Cachorro", "Gato", "Ave", "Roedor", "Outro"};
        spinnerEspecie.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, especies));

        boolean isEdit = petExistente != null;
        if (isEdit) {
            etNomePet.setText(petExistente.getNome());
            etRaca.setText(petExistente.getRaca());
            if (petExistente.getIdade() > 0) etIdade.setText(String.valueOf(petExistente.getIdade()));
            if (petExistente.getPeso()  > 0) etPeso.setText(String.valueOf(petExistente.getPeso()));

            for (int i = 0; i < especies.length; i++) {
                if (especies[i].equalsIgnoreCase(petExistente.getEspecie())) {
                    spinnerEspecie.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Editar Pet" : "Cadastrar Novo Pet")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome     = etNomePet.getText().toString().trim();
                    String especie  = spinnerEspecie.getSelectedItem().toString();
                    String raca     = etRaca.getText().toString().trim();
                    String idadeStr = etIdade.getText().toString().trim();
                    String pesoStr  = etPeso.getText().toString().trim();

                    if (TextUtils.isEmpty(nome)) {
                        Toast.makeText(this, "Nome do pet é obrigatório!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int idade = 0;
                    if (!TextUtils.isEmpty(idadeStr)) {
                        try { idade = Integer.parseInt(idadeStr); } catch (Exception ignored) {}
                    }

                    double peso = 0.0;
                    if (!TextUtils.isEmpty(pesoStr)) {
                        try { peso = Double.parseDouble(pesoStr); } catch (Exception ignored) {}
                    }

                    if (isEdit) {
                        petExistente.setNome(nome);
                        petExistente.setEspecie(especie);
                        petExistente.setRaca(raca);
                        petExistente.setIdade(idade);
                        petExistente.setPeso(peso);

                        repository.atualizarPet(petExistente, new PetCareRepository.RepositoryCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                Toast.makeText(PetsActivity.this, "Pet atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                carregarPets();
                            }

                            @Override
                            public void onError(String mensagem) {
                                Toast.makeText(PetsActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Pet novoPet = new Pet();
                        novoPet.setNome(nome);
                        novoPet.setEspecie(especie);
                        novoPet.setRaca(raca);
                        novoPet.setIdade(idade);
                        novoPet.setPeso(peso);
                        novoPet.setTutorId(sessionManager.getUserId());
                        novoPet.setTutorNome(sessionManager.getNome());

                        repository.adicionarPet(novoPet, new PetCareRepository.RepositoryCallback<Pet>() {
                            @Override
                            public void onSuccess(Pet p) {
                                Toast.makeText(PetsActivity.this, "Pet cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                carregarPets();
                            }

                            @Override
                            public void onError(String mensagem) {
                                Toast.makeText(PetsActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarExclusaoPet(Pet pet) {
        new AlertDialog.Builder(this)
                .setTitle("Remover Pet")
                .setMessage("Deseja realmente excluir o cadastro de " + pet.getNome() + "?")
                .setPositiveButton("Sim, excluir", (dialog, which) -> {
                    repository.excluirPet(pet.getId(), new PetCareRepository.RepositoryCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            Toast.makeText(PetsActivity.this, "Pet removido com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarPets();
                        }

                        @Override
                        public void onError(String mensagem) {
                            Toast.makeText(PetsActivity.this, "Erro ao remover: " + mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
