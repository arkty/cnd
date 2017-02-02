#!/usr/bin/env ruby
require 'rest-firebase'
require 'active_support/core_ext/hash/keys'
require 'json'

@effects = []
Dir["effects/*"].each { |e| @effects << eval(File.read(e)) }

def processTurn(f, battleId, source, target, card)

  f.get("battles/#{battleId}/states").each_with_index do |state, index|

    if(index == source)
      state['mana'] -= card[:mana]
    end

    if(index == target)
      state['effects'] ||= []

      card[:effects].each do |effect|
        state['effects'] << effect.clone
      end
    end

    next if state['effects'].nil?

    state['effects'].each { |e| e.symbolize_keys! }

    state['effects'].each_with_index do |fb_effect, key|

      effect = @effects.select {|e| e[:id] == fb_effect[:id]}[0]

      effect[:action].(state)

      fb_effect[:duration] -= 1
      if fb_effect[:duration] <= 0
        state['effects'].delete(fb_effect)
      end

    end

    f.put("battles/#{battleId}/states/#{index}", state)
  end
end

@board_id = '35:74:d6:65'

URL = 'https://clouds-and-droids.firebaseio.com/'

f = RestFirebase.new :site => URL, :timeout => 15,
  :log_method => method(:puts), :auth => false,
  :retry_exceptions =>
                       [IOError, SystemCallError, Timeout::Error]

@cards = f.get("cards")

@cards.each do |id, card|
  card.symbolize_keys!
  card[:effects].each { |e| e.symbolize_keys! } unless card[:effects].nil?
end

@reconnect = true
es = f.event_source("battles/#{@board_id}")
es.onopen   { |sock| p sock }
es.onerror  { |error, sock| p error }
es.onreconnect{ |error, sock| p error; @reconnect }

es.onmessage do |event, message, sock|
  next if message.nil?
  turn = message['path'][/turns\/([0-9]+)/, 1]

  unless turn.nil?
    turn = turn.to_i
    source = turn % 2
    target = message['data']['target'].to_i

    card = @cards[message['data']['card']]

    processTurn(f, @board_id, source, target, card)
  end
end

es.start
es.wait
