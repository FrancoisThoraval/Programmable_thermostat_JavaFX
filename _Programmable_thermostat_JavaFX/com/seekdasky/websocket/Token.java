
/*
 * 
 * 	THIS FILE SHOULD BE RE-WRITTEN 
 * 
 * A token have max size of 8 octet long payload so we shouldn't worry about spliting the user input (the developper should take care of it)
 * 
 * We should implement also a way to allow sending chunks of data (using the multi-frame feature of websocket)
 * 
 */
package com.seekdasky.websocket;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Token {

    // 'javax.json.JsonObject' is initialized only when the token owns the TEXT OpCode
    private javax.json.JsonObject _json_object;

    // rawString is the String representation of the payload (may not be JSON)
    private String rawString;

    // OpCode of the token, see: https://tools.ietf.org/html/rfc6455#section-5.2 for more information
    public enum OpCodes {
        STREAM((byte) 0x0), TEXT((byte) 0x1), BINARY((byte) 0x2), CONNECTION_CLOSED((byte) 0x8), PING((byte) 0x9), PONG((byte) 0xA);
        protected final byte _byte_code;

        OpCodes(byte byte_code) {
            _byte_code = byte_code;
        }

        private static OpCodes XXX(byte byte_code) {
            switch (byte_code) {
                case 0x0:
                    return STREAM;
                case 0x1:
                    return TEXT;
                case 0x2:
                    return BINARY;
                case 0x8:
                    return CONNECTION_CLOSED;
                case 0x9:
                    return PING;
                case 0xA:
                    return PONG;
                default:
                    return TEXT;
            }
        }
    }
    protected OpCodes _opCode;

    // Full binary header of the token, see https://tools.ietf.org/html/rfc6455#section-5.2 for more information
    private byte _header;

    //binary length of the payload, it may be completed by the extended payload length, see https://tools.ietf.org/html/rfc6455#section-5.2 for more informations
    private byte payloadLength;

    //masking key to encode/decode informations see https://tools.ietf.org/html/rfc6455#section-5.3 for more informations
    private byte[] mask;

    //extended payload length, see https://tools.ietf.org/html/rfc6455#section-5.2 for more informations
    private byte[] extendedPayloadLength;

    //decoded payload
    private byte[] payload;

    // End flag see https://tools.ietf.org/html/rfc6455#section-5.2 for more informations
    private boolean _is_end;

    public Token(byte[] bytes) {
        this.parseRaw(bytes);
        if (_opCode == OpCodes.TEXT) {
            try {
                _json_object = javax.json.Json.createReader(new java.io.ByteArrayInputStream(rawString.getBytes(java.nio.charset.StandardCharsets.UTF_8))).readObject();
            } catch (javax.json.JsonException je) {
                // The text is not JSON-compliant
                je.printStackTrace();
            }
        }
    }

    public Token(OpCodes opcode, boolean is_end, javax.json.JsonObject json_object) {
        _is_end = is_end;
        _opCode = opcode;
        _json_object = json_object;
    }

    public Token(OpCodes opcode, boolean is_end) {
        this(opcode, is_end, javax.json.Json.createObjectBuilder().build());
    }

    public Token(OpCodes opcode) {
        this(opcode, true);
    }

    private void parseRaw(byte[] raw) {
        this.rawString = "";
        int startIndex = 0;

        _header = raw[0];
        //   int opcode = (int) (_header & 0b00001111);
        _is_end = (int) (_header & 0b10000000) == 128;

        _opCode = OpCodes.XXX((byte) (_header & 0b00001111));

        //  _opCode = Token.getOpCOdeEnum(opcode);
        this.payloadLength = (byte) (raw[1] & 0b01111111);

        //first we determine the index of the beginning of the payload
        short payloadLen = (short) (raw[1] & 0xFF);
        //we strip the MASK value (always 1)
        payloadLen -= 128;
        if (payloadLen <= 125) {
            startIndex = 6;
            this.extendedPayloadLength = new byte[0];
        } else if (payloadLen == 126) {
            startIndex = 8;
            this.extendedPayloadLength = new byte[2];
        } else {
            startIndex = 14;
            this.extendedPayloadLength = new byte[8];
        }

        //now we get the masking key
        this.mask = new byte[4];
        System.arraycopy(raw, startIndex - 4, this.mask, 0, 4);

        //let's decode
        byte[] decoded = new byte[raw.length - startIndex];
        for (int i = startIndex; i < raw.length; i++) {
            //i MOD 4
            int maskIndex = i - startIndex & 0x3;
            decoded[i - startIndex] = (byte) (raw[i] ^ this.mask[maskIndex]);
        }

        //transform in String
        this.rawString += new String(decoded, java.nio.charset.StandardCharsets.UTF_8);

        //put raw decoded
        this.payload = decoded;
    }

//    private static byte getOpCodeBinary(OpCodes op) {
//        switch (op) {
//            case STREAM:
//                return (byte) 0x0;
//            case TEXT:
//                return (byte) 0x1;
//            case BINARY:
//                return (byte) 0x2;
//            case CONNECTION_CLOSED:
//                return (byte) 0x8;
//            case PING:
//                return (byte) 0x9;
//            case PONG:
//                return (byte) 0xA;
//            default:
//                return (byte) 0x1;
//        }
//    }
//    private static OpCodes getOpCOdeEnum(int op) {
//        switch (op) {
//            case 0:
//                return OpCodes.STREAM;
//            case 1:
//                return OpCodes.TEXT;
//            case 2:
//                return OpCodes.BINARY;
//            case 8:
//                return OpCodes.CONNECTION_CLOSED;
//            case 9:
//                return OpCodes.PING;
//            case 10:
//                return OpCodes.PONG;
//            default:
//                return OpCodes.TEXT;
//        }
//    }
    public static Token buildFromJSON(javax.json.JsonObject json_object) {
        int length = json_object.toString().length();
        Token token = new Token(Token.OpCodes.TEXT, true, json_object);
        token._header = (byte) 0b10000001;
        token.mask = new byte[0];
        if (length <= Byte.MAX_VALUE - 2) {
            token.payloadLength = (byte) length;
        } else if (length <= 65535) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(length);
            token.payloadLength = (byte) Byte.MAX_VALUE - 1;
            token.extendedPayloadLength = ltrim(b.array());
        } else { // 8 bytes
            ByteBuffer b = ByteBuffer.allocate(Byte.SIZE);
            b.putInt(length);
            token.payloadLength = (byte) Byte.MAX_VALUE;
            token.extendedPayloadLength = ltrim(b.array());
        }
        token.payload = json_object.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return token;
    }

    public byte[] getFrame() {
        ArrayList<Byte> list = new ArrayList<>();
        byte header = (byte) 0b00000000;
        if (_is_end) {
            header = (byte) 0b10000000;
        }
        header += _opCode._byte_code;
        // header += Token.getOpCodeBinary(_opCode);
        list.add(header);
        list.add(this.payloadLength);
        try {
            if (this.extendedPayloadLength.length != 0) {
                for (byte b : this.extendedPayloadLength) {
                    list.add(b);
                }
            }
        } catch (java.lang.NullPointerException npe) {
            // This means the payload is short and doesn't have extended payload length
        }

        //copy the payload into the frame
        for (byte b : this.payload) {
            list.add(b);
        }

        byte[] returned = new byte[list.size()];
        int i = 0;
        for (byte b : list) {
            returned[i++] = b;
        }

        return returned;
    }

    public void setRawString(String s) {
        if (s.length() <= 125) {
            this.payloadLength = (byte) s.length();
        } else if (s.length() <= 65535) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(s.length());
            this.payloadLength = (byte) 126;
            this.extendedPayloadLength = ltrim(b.array());
        } else {
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putInt(s.length());
            this.payloadLength = (byte) 127;
            this.extendedPayloadLength = ltrim(b.array());
        }
        this.payload = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public static byte[] rtrim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return java.util.Arrays.copyOf(bytes, i + 1);
    }

    static byte[] ltrim(byte[] bytes) {
        int i = 0;
        while (bytes[i] == (byte) 0 && i != bytes.length) {
            i++;
        }
        return java.util.Arrays.copyOfRange(bytes, i, bytes.length);
    }

    public String getString(String key) {
        return _json_object.getString(key);
    }

    public double getDouble(String key) {
        return _json_object.getJsonNumber(key).doubleValue();
    }

    public String getRaw() {
        return this.rawString;
    }

    public static ArrayList<byte[][]> splitFrames(byte[] raw) {
        boolean finished = false;
        int curPos = 0;
        boolean wasPrevSingle = true;
        ArrayList<byte[][]> returned = new ArrayList<byte[][]>();
        int multiFrameCount = 0;
        while (!finished) {

            byte[] buffer;
            ByteBuffer wrapped;
            //faire & 0xFF permet de récupérer la valeur non signée du bytes
            // a single frame format is 1xxxxxxx, so it is always >= 126, multi frames format is 0xxxxxxx so it is always < 126
            boolean singleFrame = (raw[curPos] & 0xFF) >= 126 && wasPrevSingle;

            short payloadLen = (short) (raw[curPos + 1] & 0xFF);
            //we strip the MASK value (always 1)
            payloadLen -= 128;

            int finalLength = 0;
            //no extendend payload len
            if (payloadLen <= 125) {
                //6 frames (FIN, length,masking key) + length of the payload
                finalLength = 6 + payloadLen;
            } else if (payloadLen == 126) {
                buffer = new byte[2];
                buffer[0] = raw[curPos + 2];
                buffer[1] = raw[curPos + 3];
                wrapped = ByteBuffer.wrap(buffer);
                int extendedLen = wrapped.getShort();
                finalLength = 8 + extendedLen;
            } else {
                buffer = new byte[8];
                System.arraycopy(raw, curPos + 2, buffer, curPos + 10, 8);
                wrapped = ByteBuffer.wrap(buffer);
                long extendedLen = wrapped.getLong();
                finalLength = (int) (14 + extendedLen);
            }

            if (singleFrame) {
                curPos += finalLength;
                byte[][] frame = new byte[1][finalLength];
                System.arraycopy(raw, (curPos - finalLength), frame[0], 0, finalLength);
                returned.add(frame);
                wasPrevSingle = true;
            } else {
                //get first byte to check if it is the last frame of the group
                buffer = new byte[1];
                buffer[0] = raw[curPos];
                wrapped = ByteBuffer.wrap(buffer);

                curPos += finalLength;
                byte[][] multiframe = returned.get(returned.size());
                System.arraycopy(raw, curPos - finalLength, multiframe[multiFrameCount], curPos - 1, finalLength);

                //check if it's the last frame of the group
                if (wrapped.getShort() >= 126) {
                    wasPrevSingle = true;
                    multiFrameCount = 0;
                } else {
                    multiFrameCount++;
                    wasPrevSingle = false;
                }
            }
            finished = curPos >= raw.length;
        }
        return returned;
    }

    public boolean isStreamToken() {
        return _is_end == false || _is_end && _opCode == Token.OpCodes.STREAM;
    }

}
