package org.example.paneljavafx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.paneljavafx.dao.ClienteDAO;
import org.example.paneljavafx.dao.PosicionDAO;
import org.example.paneljavafx.dao.impl.ClienteImpl;
import org.example.paneljavafx.dao.impl.PosicionImpl;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Posicion;
import org.example.paneljavafx.model.Transaccion;

import java.util.ArrayList;
import java.util.List;

public class ClienteService {

    // ========================= SINGLETON =========================
    private static final ClienteService INSTANCE = new ClienteService();

    public static ClienteService getInstance() {
        return INSTANCE;
    }

    private ClienteService() {}

    // ========================= DAO =========================
    private final ClienteDAO clienteDAO = new ClienteImpl();
    private final PosicionDAO posicionDAO = new PosicionImpl();

    // ========================= CACHE =========================
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    // ========================= LOAD =========================
    public void load() {

        try {
            List<Cliente> data = clienteDAO.findAll();

            for (Cliente c : data) {

                // 🔥 SOLO posiciones aquí (no transacciones)
                List<Posicion> posiciones = posicionDAO.findByClienteId(c.getIdCliente());

                // inicializa lista vacía segura
                if (posiciones == null) {
                    posiciones = new ArrayList<>();
                }

                c.setPosiciones(posiciones);
            }

            clientes.setAll(data);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando clientes o posiciones desde BD");
        }
    }

    // ========================= GETTERS =========================
    public ObservableList<Cliente> getAll() {
        return clientes;
    }

    public Cliente getById(int id) {
        return clientes.stream()
                .filter(c -> c.getIdCliente() == id)
                .findFirst()
                .orElse(null);
    }

    public long count() {
        return clientes.size();
    }

    // ========================= SEARCH =========================
    public List<Cliente> search(String query) {

        if (query == null || query.isBlank()) {
            return clientes;
        }

        String q = query.toLowerCase();

        return clientes.stream()
                .filter(c ->
                        c.getNombre().toLowerCase().contains(q) ||
                                c.getApellido().toLowerCase().contains(q) ||
                                c.getEmail().toLowerCase().contains(q)
                )
                .toList();
    }

    public boolean existsEmail(String email) {
        return clientes.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    // ========================= POSICIONES =========================
    public List<Posicion> getPosicionesByClienteId(int clienteId) {

        try {
            List<Posicion> posiciones = posicionDAO.findByClienteId(clienteId);

            return posiciones != null ? posiciones : new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ========================= CRUD =========================
    public void save(Cliente cliente) {

        Cliente saved = clienteDAO.save(cliente);

        if (saved != null) {
            clientes.add(saved);
        }
    }

    public void update(Cliente cliente) {

        clienteDAO.update(cliente);

        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdCliente() == cliente.getIdCliente()) {
                clientes.set(i, cliente);
                return;
            }
        }
    }

    public void delete(int idCliente) {

        clienteDAO.delete(idCliente);
        clientes.removeIf(c -> c.getIdCliente() == idCliente);
    }

    // ========================= POSICIONES CRUD =========================
    public void addPosition(int clienteId, Posicion posicion) {

        Cliente cliente = getById(clienteId);
        if (cliente == null) return;

        try {
            posicionDAO.save(clienteId, posicion);

            if (cliente.getPosiciones() == null) {
                cliente.setPosiciones(new ArrayList<>());
            }

            cliente.getPosiciones().add(posicion);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePosition(int clienteId, Posicion updatedPosition) {

        Cliente cliente = getById(clienteId);
        if (cliente == null || cliente.getPosiciones() == null) return;

        List<Posicion> posiciones = cliente.getPosiciones();

        for (int i = 0; i < posiciones.size(); i++) {

            Posicion p = posiciones.get(i);

            if (p.getIdFondo().equals(updatedPosition.getIdFondo())) {

                posiciones.set(i, updatedPosition);

                try {
                    posicionDAO.update(updatedPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }
        }
    }

    public long contarFondosUnicos(Cliente cliente) {

        if (cliente == null || cliente.getPosiciones() == null) return 0;

        return cliente.getPosiciones().stream()
                .map(Posicion::getIdFondo)
                .filter(id -> id != null)
                .distinct()
                .count();
    }

    public double calcularCartera(Cliente cliente) {

        if (cliente == null || cliente.getPosiciones() == null) return 0;

        return cliente.getPosiciones().stream()
                .filter(p -> p.getTransacciones() != null)
                .flatMap(p -> p.getTransacciones().stream())
                .mapToDouble(Transaccion::getImporte)
                .sum();
    }
}