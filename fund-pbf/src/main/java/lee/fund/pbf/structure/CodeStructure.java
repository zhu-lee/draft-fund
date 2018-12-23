package lee.fund.pbf.structure;

import lee.fund.pbf.structure.Coded;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 12:34
 * Desc:
 */
public interface CodeStructure {
    Coded get(Class<?> cls);
}
