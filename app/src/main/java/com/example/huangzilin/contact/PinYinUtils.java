package com.example.huangzilin.contact;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

final class PinYinUtils {
    /**
     * 获得汉字的全部拼音
     * 例如：你好 ~ nihao
     */
    public static String getPinYin(String hanzi) {
        StringBuilder pinyin = new StringBuilder();

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();//控制转换是否大小写，是否带音标
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//大写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        //由于不能直接对多个汉字转换，只能对单个汉字转换
        char[] arr = hanzi.toCharArray();
        for (char anArr : arr) {
            if (Character.isWhitespace(anArr)) continue;//如果是空格，则不处理，进行下次遍历

            //汉字是2个字节存储，肯定大于127，所以大于127就可以当为汉字转换
            if (anArr > 127) {
                try {
                    //由于多音字的存在，单 dan shan
                    String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(anArr, format);

                    if (pinyinArr != null) {
                        pinyin.append(pinyinArr[0]);
                    } else {
                        pinyin.append(anArr);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    //不是正确的汉字
                    pinyin.append(anArr);
                }
            } else {
                //不是汉字，
                pinyin.append(anArr);
            }
        }
        return pinyin.toString();
    }
}
