/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.underfs.s3a;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.DigestOutputStream;

/**
 * Unit tests for the {@link S3AOutputStream}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(S3AOutputStream.class)
public class S3AOutputStreamTest {
  private static final String BUCKET_NAME = "testBucket";
  private static final String KEY = "testKey";

  private File mFile;
  private BufferedOutputStream mLocalOutputStream;
  private S3AOutputStream mStream;

  /**
   * Sets the properties and configuration before each test runs.
   */
  @Before
  public void before() throws Exception {
    mFile = Mockito.mock(File.class);
    mLocalOutputStream = Mockito.mock(BufferedOutputStream.class);
    TransferManager manager = Mockito.mock(TransferManager.class);
    Upload result = Mockito.mock(Upload.class);

    Mockito.when(manager.upload(Mockito.any(PutObjectRequest.class))).thenReturn(result);
    PowerMockito.whenNew(BufferedOutputStream.class)
        .withArguments(Mockito.any(DigestOutputStream.class)).thenReturn(mLocalOutputStream);
    PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(mFile);
    FileOutputStream outputStream = PowerMockito.mock(FileOutputStream.class);
    PowerMockito.whenNew(FileOutputStream.class).withArguments(mFile).thenReturn(outputStream);
    mStream = new S3AOutputStream(BUCKET_NAME, KEY, manager);
  }

  /**
   * Tests to ensure {@link S3AOutputStream#write(int)} calls the underlying output stream.
   */
  @Test
  public void writeByteTest() throws Exception {
    mStream.write(1);
    mStream.close();
    Mockito.verify(mLocalOutputStream).write(1);
  }

  /**
   * Tests to ensure {@link S3AOutputStream#write(byte[])} calls the underlying output stream.
   */
  @Test
  public void writeByteArrayTest() throws Exception {
    byte[] b = new byte[10];
    mStream.write(b);
    mStream.close();
    Mockito.verify(mLocalOutputStream).write(b, 0, b.length);
  }

  /**
   * Tests to ensure {@link S3AOutputStream#write(byte[], int, int)} calls the underlying
   * output stream.
   */
  @Test
  public void writeByteArrayWithRangeTest() throws Exception {
    byte[] b = new byte[10];
    mStream.write(b, 0, b.length);
    mStream.close();
    Mockito.verify(mLocalOutputStream).write(b, 0, b.length);
  }

  /**
   * Tests to ensure {@link File#delete()} is called when the stream is closed.
   */
  @Test
  public void closeTest() throws Exception {
    mStream.close();
    Mockito.verify(mFile).delete();
  }

  /**
   * Tests to ensure {@link S3AOutputStream#flush()} calls the underlying output stream.
   */
  @Test
  public void flushTest() throws Exception {
    mStream.flush();
    mStream.close();
    Mockito.verify(mLocalOutputStream).flush();
  }
}
