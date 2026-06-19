import androidx.lifecycle.ViewModel
import com.dsm.miprofeenlinea.data.repository.TareaRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.dsm.miprofeenlinea.model.Tarea

class HomeViewModel : ViewModel() {

    private val repository = TareaRepository()
    private val db = FirebaseFirestore.getInstance()

    fun guardarTarea(
        tarea: Tarea,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        repository.guardarTarea(tarea, onSuccess, onError)
    }

    fun escucharTareas(onResult: (List<Tarea>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("tareas")
            .addSnapshotListener { snapshot, _ ->

                val tareas = snapshot?.documents?.mapNotNull {
                    it.toObject(Tarea::class.java)?.copy(id = it.id)
                } ?: emptyList()

                onResult(tareas)
            }
    }

    fun asignarTarifa(
        tareaId: String,
        tarifa: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("tareas")
            .document(tareaId)
            .update(
                mapOf(
                    "tarifa" to tarifa,
                    "estado" to "en_revision"
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun proponerTarifa(
        tareaId: String,
        tarifa: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("tareas")
            .document(tareaId)
            .update(
                mapOf(
                    "tarifa" to tarifa,
                    "estado" to "tarifa_propuesta",
                    "tarifaTimestamp" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

}