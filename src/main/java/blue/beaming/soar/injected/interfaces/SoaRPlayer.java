package blue.beaming.soar.injected.interfaces;

public interface SoaRPlayer {
    boolean soar$isStanding();
    void soar$setStanding(boolean standing);

    boolean soar$onMountAndStanding();

    float soar$ridingSittingDifference();
}
