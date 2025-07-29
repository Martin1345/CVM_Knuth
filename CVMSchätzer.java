import java.util.*;
import java.util.stream.Stream;

public class CVMSchätzer {
    private final int s; 
    //Größe des Puffers, je größer, desto genauer die Schätzung
    private final Random random;
    // Zufallszahlengenerator für die Erzeugung von Zufallswerten je Element
    private final TreeMap<String, Double> Puffer;
    // Erschaffung einer TreeMap, um die Elemente im Puffer zu speichern
    private double p;
    // Wahrscheinlichkeit, dass ein Element in den Puffer aufgenommen wird vorerst auf 1,0 gesetzt

    public CVMSchätzer(int Puffergröße) {
        // Erstellung eines Konstruktors, der die Puffergröße initialisiert
        this.s = Puffergröße;
        // Initialisierung der Puffergröße mit dem übergebenen Wert
        this.random = new Random();
        // Erstellung eines neuen Zufallszahlengenerators um die Zufallswerte je Element zu generieren
        this.Puffer = new TreeMap<>();
        // Erstellung des Puffers als TreeMap, um die Elemente und ihre Zufallswerte zu speichern
        this.p = 1.0;
        // Initialisierung der Wahrscheinlichkeit p auf 1.0, was bedeutet, dass jedes Element zunächst mit Sicherheit in den Puffer aufgenommen wird
    }

    // Ausführung des Algorithmus zur Schätzung der Anzahl verschiedener Elemente
    public double SchätzungGesehen(Stream<String> stream) {
        // Methode zur Schätzung der Anzahl verschiedener Elemente in einem Stream
        List<String> gesehenesElement = new ArrayList<>();
        // Liste, um die bereits gesehenen Elemente zu speichern;
        stream.forEach(element -> {
            //Iteration über alle Elemente im Datenstrom
            gesehenesElement.add(element);
            //Hinzufügen des aktuellen Elements zur Liste der gesehenen Elemente
            Verarbeitung(element);
            //Verarbeitung des Elements durch die Methode Verarbeitung
        });
        return Puffer.size() / p;
    }

    // Verarbeitung jedes einzelnen Elements
    private void Verarbeitung(String a) {
        // Methode zur Verarbeitung eines einzelnen Elements
        Puffer.remove(a);
        //Entfernung des Elements a aus dem Puffer, falls es bereits vorhanden ist

        double u = random.nextDouble(); 
        // Generierung eines Zufallswerts u zwischen 0 und 1 für das Element a uniform verteilt
        if (u > p) return;
        // Prüfung, ob der Zufallswert u größer ist als die Wahrscheinlichkeit p, dann wird das Element nicht weiter verarbeitet

        if (Puffer.size() < s) {
            // Prüfung, ob der Puffer noch nicht voll ist, im Fall dass u<=p
            Puffer.put(a, u);
            // Falls der Puffer nicht voll ist, wird das Element a mit seinem Zufallswert u in den Puffer eingefügt
        } else {
            // Ist der Puffer voll, wird das Element mit dem höchsten Zufallswert gesucht und gegebenenfalls ersetzt
            String maxWert = null;
            // Speicherung des Schlüssels des Elements mit dem höchsten Zufallswert
            double maxU = -1.0;
            // Initialisierung des höchsten Zufallswerts mit einem Wert kleiner als 0
            for (Map.Entry<String, Double> entry : Puffer.entrySet()) {
                // Iteration über alle Einträge im Puffer
                if (entry.getValue() > maxU) {
                    // Wenn der Zufallswert des aktuellen Eintrags größer ist als der bisher höchste
                    maxWert = entry.getKey();
                    // Aktualisierung des Schlüssels des Elements mit dem höchsten Zufallswert
                    maxU = entry.getValue();
                    // Aktualisierung des höchsten Zufallswerts
                }
            }

            if (u > maxU) {
                // Wenn der neue Zufallswert u größer ist als der höchste Zufallswert im Puffer
                p = u;
                // Aktualisierung der Wahrscheinlichkeit p auf den neuen Zufallswert u
            } else {// Wenn der neue Zufallswert u nicht größer ist als der höchste Zufallswert
            Puffer.remove(maxWert);
            //Entfernung des Elements mit dem höchsten Zufallswert aus dem Puffer
            Puffer.put(a, u);
            // Einfügen des neuen Elements a mit seinem Zufallswert u in den Puffer
            p = maxU;
            // Aktualisierung der Wahrscheinlichkeit p auf den höchsten Zufallswert im Puffer
            }
        }
    }

    // Beispielnutzung
    public static void main(String[] args) {
        CVMSchätzer estimator = new CVMSchätzer(2); 
        // Anlegen eines Schätzers mit Puffergröße 2
        List<String> streamData = Arrays.asList("apple", "banana", "apple", "cherry", "banana", "durian", "apple", "banana");
        // Beispiel Datenstrom mit einigen wiederholten Elementen
        double result = estimator.SchätzungGesehen(streamData.stream());
        // Schätzung der Anzahl verschiedener Elemente im Datenstrom
        System.out.printf("Geschätzte Anzahl verschiedener Elemente: %.2f%n", result);
        // Ausgabe der geschätzten Anzahl verschiedener Elemente
    }
}
