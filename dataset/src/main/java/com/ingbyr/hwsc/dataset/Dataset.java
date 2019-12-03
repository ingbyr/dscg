package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.BestQos;
import com.ingbyr.hwsc.common.util.WorkDir;
import lombok.Getter;

import java.nio.file.Path;

public enum Dataset {

    wsc2008_01("2008", "01", new BestQos(5.897078505371547, -5.1098901098901095, -4.408602150537635, -3.581818181818182, 5.97651985571292, 4.9015228426395945)),
    wsc2008_02("2008", "02", new BestQos(2.2653915086464407, -1.0638297872340425, -2.159574468085106, -0.9090909090909094, 0.6216200483746325, 1.5997993981945837)),
    wsc2008_03("2008", "03", new BestQos(13.18808164086065, -17.925531914893625, -17.712765957446802, -19.672727272727272, 17.62983364415065, 18.755755755755754)),
    wsc2008_04("2008", "04", new BestQos(3.704581729226821, -2.978723404255318, -3.510638297872341, -5.236363636363637, 4.0912356898572595, 6.3997995991983965)),
    wsc2008_05("2008", "05", new BestQos(9.167912333911035, -12.680851063829788, -10.904255319148936, -9.436363636363637, 10.963208123454072, 12.29329329329329)),
    wsc2008_06("2008", "06", new BestQos()),
    wsc2008_07("2008", "07", new BestQos()),
    wsc2008_08("2008", "08", new BestQos()),
    wsc2009_01("2009", "01", new BestQos(2.591202917915568, -1.6489361702127665, -1.2234042553191493, -0.5090909090909095, 1.416434406342612, 1.9929859719438876)),
    wsc2009_02("2009", "02", new BestQos()),
    wsc2009_03("2009", "03", new BestQos()),
    wsc2009_04("2009", "04", new BestQos()),
    wsc2009_05("2009", "05", new BestQos());

    @Getter
    private final String datasetId1;

    @Getter
    private final String datasetId2;

    @Getter
    private final BestQos bestQos;

    @Getter
    private Path path;

    Dataset(String datasetId1, String datasetId2, BestQos bestQos) {
        this.datasetId1 = datasetId1;
        this.datasetId2 = datasetId2;
        this.path = WorkDir.WORK_DIR.resolve("data")
                .resolve("wsc" + datasetId1)
                .resolve("Testset" + datasetId2)
                .normalize();
        this.bestQos = bestQos;
    }
}
