package app.com.example.android.textbulletpointer;

public class BulletPointWizard {
    public String[][] getBulletPoints() {
        String paragraph = "Bacon ipsum dolor amet andouille rump cow pork belly drumstick. " +
                "Prosciutto strip steak hamburger swine salami kevin alcatra t-bone tri-tip " +
                "andouille shoulder tail turkey jowl cow. Chicken brisket tongue, hamburger " +
                "short loin turkey shankle pancetta leberkas ham bacon. Swine flank venison, " +
                "sirloin filet mignon tongue tenderloin corned beef t-bone ham hock. Pancetta " +
                "sausage t-bone salami pork loin.";
        String bulletPoint = "Chicken brisket tongue, hamburger short loin turkey shankle pancetta " +
                "leberkas ham bacon.";

        String[] line = { paragraph, bulletPoint };
        String[][] data = new String[5][];

        for (int i = 0; i < data.length; i += 1) {
            data[i] = line;
        }

        return data;
    }
}
