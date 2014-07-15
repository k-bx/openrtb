/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.openrtb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.TestExt;

import org.junit.Test;

/**
 * Tests for {@link ProtoUtils}.
 */
public class ProtoUtilsTest {

  @Test
  public void testFilter() {
    TestExt.Test1 test1 = TestExt.Test1.newBuilder().setTest1("test1").build();
    TestExt.Test2 test2 = TestExt.Test2.newBuilder().setTest2("test2").build();
    BidRequest reqPlainClear = BidRequest.newBuilder()
        .setId("0")
        .addImp(Impression.newBuilder().setId("1"))
        .build();
    BidRequest reqPlainNoClear = BidRequest.newBuilder()
        .setId("0")
        .addImp(Impression.newBuilder().setId("1")
            .setBanner(Banner.newBuilder()))
        .build();
    BidRequest reqExt = BidRequest.newBuilder()
        .setId("0")
        .addImp(Impression.newBuilder()
            .setId("1")
            .setBanner(Banner.newBuilder()
                .setExtension(TestExt.testBanner, test1))
            .setExtension(TestExt.testImp, test1))
        .setExtension(TestExt.testRequest1, test1)
        .setExtension(TestExt.testRequest2, test2)
        .build();
    BidRequest reqDiff = reqPlainClear.toBuilder().setId("1").build();
    assertEquals(reqPlainClear, ProtoUtils.filter(reqExt, true, ProtoUtils.NOT_EXTENSION));
    assertEquals(reqPlainNoClear, ProtoUtils.filter(reqExt, false, ProtoUtils.NOT_EXTENSION));
    assertEquals(
        ImmutableList.of(reqPlainClear),
        ProtoUtils.filter(ImmutableList.of(reqPlainClear, reqDiff), new Predicate<BidRequest>() {
          @Override public boolean apply(BidRequest req) {
          return "0".equals(req.getId());
        }}));
  }

  @Test
  public void testUpdate() {
    BidRequest.Builder req = BidRequest.newBuilder().setId("0");
    ProtoUtils.update(ImmutableList.of(req), new Function<BidRequest.Builder, Boolean>() {
      @Override public Boolean apply(BidRequest.Builder req) {
        req.setAt(1);
        return true;
      }});
    assertTrue(req.getAt() == 1);
  }

  @Test
  public void testBuilderConversions() {
    BidRequest.Builder reqBuilder = BidRequest.newBuilder().setId("0");
    BidRequest req = reqBuilder.build();
    assertEquals(req, ProtoUtils.built(reqBuilder));
    assertSame(req, ProtoUtils.built(req));
    assertNull(ProtoUtils.built(null));
    assertEquals(reqBuilder.build(), ProtoUtils.builder(req).build());
    assertSame(reqBuilder, ProtoUtils.builder(reqBuilder));
    assertNull(ProtoUtils.builder(null));
  }
}
