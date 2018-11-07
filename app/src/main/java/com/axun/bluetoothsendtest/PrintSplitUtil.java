package com.axun.bluetoothsendtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.gprinter.command.EscCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.GpUtils;

import java.util.Vector;

public class PrintSplitUtil {
    private static final String PRINT_LINE = "------------------------------------------------\n";
    public static final int PRINT_TOTAL_LENGTH = 48 * 3;
    public static final short PRINT_POSITION_0 = 0;
    public static final short PRINT_POSITION_1 = 26 * 3;
    public static final short PRINT_POSITION_2 = 32 * 3;
    public static final short PRINT_POSITION_3 = 42 * 3;
    public static final int MAX_GOODS_NAME_LENGTH = 22 * 3;
    public static final short PRINT_UNIT = 43;


    public static String getPrintText(Context context, GoodsListInfo goodsInfo, String store, String userMobile, String qrCode) {

        EscCommand esc = new EscCommand();
        esc.addInitializePrinter(); // 顶部图片
        esc.addSelectJustification(JUSTIFICATION.CENTER);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        esc.addRastBitImage(b, 200, 0); // 打印图片
        esc.addPrintAndLineFeed();
        esc.addText(PRINT_LINE); // 订单信息
        if (!TextUtils.isEmpty(store)) {
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);
            esc.addText(store + "\n"); // 打印文字
        }
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF); // 头部信息
        esc.addText("打印编号：" + goodsInfo.express_sn);
        esc.addPrintAndLineFeed();
        esc.addText("操作时间：" + DateTimeUtil.getCurrentDateTime());
        esc.addPrintAndLineFeed();
        esc.addText("操作员：" + userMobile);
        esc.addPrintAndLineFeed();
        esc.addText(PRINT_LINE); // 商品头信息

        esc.addSetHorAndVerMotionUnits((byte) PRINT_UNIT, (byte) 0);
        esc.addText("商品名");
        esc.addSetAbsolutePrintPosition(PRINT_POSITION_1);
        esc.addText("单价");
        esc.addSetAbsolutePrintPosition(PRINT_POSITION_2);
        esc.addText("数量");
        esc.addSetAbsolutePrintPosition(PRINT_POSITION_3);
        esc.addText("金额");
        esc.addPrintAndLineFeed(); // 商品信息
        if (goodsInfo.goods_list != null && goodsInfo.goods_list.size() > 0) {
            esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.OFF, ENABLE.OFF);
            for (int i = 0; i < goodsInfo.goods_list.size(); i++) {
                GoodsListInfo.GoodsListBean goods = goodsInfo.goods_list.get(i);
                String[] goodsNames = SubByteString.getSubedStrings(goods.goods_name, 20);

                if (goodsNames != null && goodsNames.length > 0) {
                    esc.addText((i + 1) + "." + goodsNames[0]);
                } else {
                    esc.addText((i + 1) + "." + goods.goods_name);
                }
                esc.addSetHorAndVerMotionUnits((byte) PRINT_UNIT, (byte) 0); // 单价
                short priceLength = (short) goods.goods_price.length();
                short pricePosition = (short) (PRINT_POSITION_1 + 12 - priceLength * 3);
                esc.addSetAbsolutePrintPosition(pricePosition);
                esc.addText(goods.goods_price);      // 单价还未获取 // 数量
                short numLength = (short) (goods.goods_num + goods.goods_unit).getBytes().length;
                short numPosition = (short) (PRINT_POSITION_2 + 14 - numLength * 3);
                esc.addSetAbsolutePrintPosition(numPosition);
                esc.addText(goods.goods_num + goods.goods_unit); // 金额
                short amountLength = (short) goods.goods_amount.replace(" ", "").getBytes().length;
                short amountPosition = (short) (PRINT_POSITION_3 + 11 - amountLength * 3);
                esc.addSetAbsolutePrintPosition(amountPosition);
                esc.addText(goods.goods_amount);
                if (goodsNames == null || goodsNames.length == 0) {
                    esc.addPrintAndLineFeed();
                } else if (goodsNames != null && goodsNames.length > 1) {
                    for (int j = 1; j < goodsNames.length; j++) {
                        esc.addText("" + goodsNames[j]);
                        esc.addPrintAndLineFeed();
                    }
                }
            }
            esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);
            esc.addText(PRINT_LINE);
        } // 总计信息
        esc.addSelectJustification(JUSTIFICATION.RIGHT);// 设置打印居右
        if (!TextUtils.isEmpty(goodsInfo.subsidy)) {
            esc.addText("优惠补贴：" + goodsInfo.subsidy + "元\n");
        }
        if (!TextUtils.isEmpty(goodsInfo.goods_amount)) {
            esc.addText("金额总计：" + goodsInfo.goods_amount + "元\n");
        }
        if (!TextUtils.isEmpty(goodsInfo.order_amount)) {
            esc.addText("还需支付：" + goodsInfo.order_amount + "元\n");
        }
        esc.addText(PRINT_LINE); // 打印二维码
        if (!TextUtils.isEmpty(qrCode)) {
            esc.addPrintAndLineFeed();
            esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
            esc.addText("请打开微信，扫码付款\n");
            esc.addPrintAndLineFeed(); // 48 49 50 51
            esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
            esc.addSelectSizeOfModuleForQRCode((byte) 7);// 设置qrcode模块大小
            esc.addStoreQRCodeData(qrCode);// 设置qrcode内容
            esc.addPrintQRCode();// 打印QRCode
            esc.addPrintAndLineFeed();
            esc.addText("请将二维码放平整后再扫码\n");
        }
        esc.addPrintAndFeedLines((byte) 3); // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus(); // 最终数据
        Vector<Byte> datas = esc.getCommand();
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String result = Base64.encodeToString(bytes, Base64.DEFAULT);
        return result;
    }

}
