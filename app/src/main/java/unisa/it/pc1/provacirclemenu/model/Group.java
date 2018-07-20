package unisa.it.pc1.provacirclemenu.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {

    private String group_id;
    private String nome;
    private String immagine;
    private ArrayList<User> utenti;

    public Group() {
    }

    public Group(String group_id, String nome, String immagine, ArrayList<User> utenti) {
        this.group_id = group_id;
        this.nome = nome;
        this.immagine = immagine;
        this.utenti = utenti;
    }

    public Group(String group_id, String nome, String immagine) {
        this.group_id = group_id;
        this.nome = nome;
        this.immagine = immagine;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public ArrayList<User> getUtenti() {
        return utenti;
    }

    public void setUtenti(ArrayList<User> utenti) {
        this.utenti = utenti;
    }
}
