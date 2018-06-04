package unisa.it.pc1.provacirclemenu;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Antonio on 22/03/2018.
 */

public class Task implements Serializable{

    private String taskId;

    private String contenuto;
    private Date data;
    private int foto;

    private Date deadline;
    private String descrizione;
    private String categoria;

    public Task(String contenuto, Date data, int foto, String taskId) {
        this.contenuto = contenuto;
        this.data = data;
        this.foto = foto;
        this.taskId = taskId;
    }

    public Task(String contenuto, Date data,String taskId) {
        this.contenuto = contenuto;
        this.data = data;
        this.taskId = taskId;
    }

    public Task(String contenuto, Date data, int foto, Date deadline, String descrizione, String categoria,String taskId) {
        this.contenuto = contenuto;
        this.data = data;
        this.foto = foto;
        this.deadline = deadline;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.taskId = taskId;
    }

    public Task(){

    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String id) {
        taskId = id;
    }
}
