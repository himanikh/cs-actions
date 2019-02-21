/*
 * (c) Copyright 2019 Micro Focus, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudslang.content.tesseract.utils;

public class Descriptions {
    public static class Common {
        public static final String RETURN_CODE_DESC = "0 if success, -1 otherwise.";
        public static final String EXCEPTION_DESC = "In case of success response, this result is empty. In case of " +
                "failure response, this result contains the java stack trace of the runtime exception.";
    }

    public static class ExtractText {
        public static final String EXTRACT_TEXT_DESC = "This operation extracts the text from a specified file given " +
                "as input using Tesseract's OCR library.";
        public static final String RETURN_RESULT_DESC = "This will contain the extracted text.";
        public static final String SUCCESS_DESC = "Text extracted successfully.";
        public static final String FAILURE_DESC = "There was an error while trying to extract the text.";
    }

    public static class InputsDescription {
        public static final String FILE_PATH_DESC = "The path to the file from where the text needs to be extracted.";
        public static final String DATA_PATH_DESC = "The path to the Tesseract data config directory. This directory" +
                " can contain different configuration files as well as trained language data files.";
        public static final String LANGUAGE_DESC = "The language that will be used by the OCR engine. This input is " +
                "taken into consideration only when specifying the dataPath input as well.";
        public static final String PDF_FILE_PATH_DESC = "The path to the PDF file from where the text needs to be " +
                "extracted.";
        public static final String DPI_DESC = "The dpi value when converting the PDF file to image.  ";
    }

    public static class PdfConvert {
        public static final String PDF_CONVERT_DESC = "This operation converts a PDF file from a given path to an " +
                "image file and then extracts the text with the help of Extract Text operation";
    }
}